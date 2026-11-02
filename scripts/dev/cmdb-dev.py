#!/usr/bin/env python3
"""Persistent local development SQL Server; no reset on startup, no production configuration."""
import argparse
import json
import os
from pathlib import Path
import secrets
import socket
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[2]
PRIVATE = Path(os.environ.get('CMDB_DEV_CONFIG_DIR', str(Path.home()/'.config/cmdb/dev'))).resolve()
COMPOSE = ['docker', 'compose', '-f', str(ROOT/'infra/dev/compose.yaml')]
VOLUME = 'cmdb-dev-sql-data'


def run(cmd, **kwargs):
    return subprocess.run(cmd, check=True, text=True, **kwargs)


def configuration():
    config = json.loads((PRIVATE/'config.json').read_text())
    if config['database'] != 'cmdb_dev' or config['sql_username'] != 'cmdb_dev_app' or config['app_username'] != 'devadmin':
        raise RuntimeError('Database and identity names must match config.example.json')
    if config.get('accept_eula') != 'Y':
        raise RuntimeError('Explicit SQL Server EULA acceptance required in private config')
    for key in ['sql_port', 'backend_port']:
        if not isinstance(config[key], int) or not 1024 <= config[key] <= 65535:
            raise RuntimeError('Invalid local port: '+key)
    for name in ['sa-password', 'db-password', 'app-password']:
        path = PRIVATE/name
        if path.stat().st_mode & 0o077:
            raise RuntimeError('Secret permissions must be 0600: '+name)
    env = os.environ.copy()
    env.update(CMDB_DEV_CONFIG_DIR=str(PRIVATE), CMDB_DEV_SQL_PORT=str(config['sql_port']), CMDB_DEV_ACCEPT_EULA='Y')
    return config, env


def compose(*args, capture=False):
    _, env = configuration()
    return run(COMPOSE+list(args), env=env, capture_output=capture)


def configure():
    PRIVATE.mkdir(parents=True, exist_ok=True, mode=0o700)
    if PRIVATE.stat().st_mode & 0o077:
        raise RuntimeError('Private configuration directory must have mode 0700')
    if not (PRIVATE/'config.json').exists():
        with (PRIVATE/'config.json').open('x') as f:
            f.write((ROOT/'infra/dev/config.example.json').read_text())
        (PRIVATE/'config.json').chmod(0o600)
    for name, size in [('sa-password', 32), ('db-password', 32), ('app-password', 12)]:
        path = PRIVATE/name
        if not path.exists():
            fd = os.open(path, os.O_WRONLY | os.O_CREAT | os.O_EXCL, 0o600)
            with os.fdopen(fd, 'w') as f:
                f.write('D!a7'+secrets.token_hex(size//2))
    print('Private configuration prepared at', PRIVATE, '; existing secrets preserved')


def database(action):
    _, env = configuration()
    cache = ROOT/'target/cmdb-dev-driver-classpath.txt'
    if not cache.exists():
        cache.parent.mkdir(exist_ok=True)
        run(['./mvnw', '-B', 'dependency:build-classpath', '-DincludeScope=runtime', '-Dmdep.outputFile='+str(cache)],
            cwd=ROOT, stdout=subprocess.DEVNULL)
    # Only the SQL Server driver is needed. Production/test configuration is never loaded here.
    driver = [p for p in cache.read_text().strip().split(os.pathsep) if '/mssql-jdbc/' in p]
    if len(driver) != 1:
        raise RuntimeError('Expected exactly one SQL Server JDBC driver')
    return run(['java', '--class-path', driver[0], 'scripts/dev/DevDatabase.java', action],
               cwd=ROOT, env=env, capture_output=True).stdout.strip()


def up():
    config, _ = configuration()
    existing = json.loads(compose('ps', '-a', '--format', 'json', capture=True).stdout or '[]')
    if isinstance(existing, dict):
        existing = [existing]
    volume = subprocess.run(['docker', 'volume', 'inspect', VOLUME], capture_output=True, text=True)
    if volume.returncode == 0:
        labels = json.loads(volume.stdout)[0].get('Labels') or {}
        if labels.get('com.docker.compose.project') != 'cmdb-dev' or labels.get('cmdb.purpose') != 'persistent-development':
            raise RuntimeError('Dedicated volume name already exists with different ownership; no resources changed')
    if not any(x.get('State') == 'running' for x in existing):
        with socket.socket() as s:
            s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
            try:
                s.bind(('127.0.0.1', config['sql_port']))
            except OSError:
                raise RuntimeError('SQL host port is already occupied; choose another sql_port')
    compose('config', '--quiet')
    compose('up', '-d', '--wait', '--wait-timeout', '180')
    print(database('init'))
    print(database('inspect'))


def backend():
    config, env = configuration()
    with socket.socket() as s:
        s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        try:
            s.bind(('127.0.0.1', config['backend_port']))
        except OSError:
            raise RuntimeError('Backend port occupied; choose another backend_port in private config')
    env = {k:v for k,v in env.items() if not k.startswith(('SPRING_', 'CMDB_')) and k not in ['JAVA_TOOL_OPTIONS','JDK_JAVA_OPTIONS','_JAVA_OPTIONS']}
    env.update(SPRING_PROFILES_ACTIVE='dev', CMDB_BD_SERVIDOR='127.0.0.1', CMDB_BD_PUERTO=str(config['sql_port']),
               CMDB_BD_NOMBRE='cmdb_dev', CMDB_DB_USERNAME='cmdb_dev_app',
               CMDB_DB_PASSWORD=(PRIVATE/'db-password').read_text().strip())
    os.chdir(ROOT)
    os.execvpe('java', ['java', '-jar', 'target/cmdb-backend-0.0.1-SNAPSHOT.jar',
                      '--server.address=127.0.0.1', '--server.port='+str(config['backend_port'])], env)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('action', choices=['configure','up','down','restart','status','init','inspect','snapshot','marker-remove','backend'])
    args = parser.parse_args()
    os.chdir(ROOT)
    if args.action == 'configure': configure()
    elif args.action == 'up': up()
    elif args.action == 'down': compose('down')  # Deliberately never passes --volumes.
    elif args.action == 'restart':
        compose('stop'); up()
    elif args.action == 'status': compose('ps')
    elif args.action == 'backend': backend()
    else: print(database(args.action))

if __name__ == '__main__':
    try: main()
    except (RuntimeError, FileNotFoundError, subprocess.CalledProcessError) as e:
        if isinstance(e, subprocess.CalledProcessError):
            print('Development operation failed (exit '+str(e.returncode)+').', file=sys.stderr)
            # Helper messages are deliberately sanitized; never echo arbitrary SQL or secrets.
            if e.stderr and e.cmd[0] == 'java': print(e.stderr.strip(), file=sys.stderr)
        else: print(str(e), file=sys.stderr)
        sys.exit(1)
