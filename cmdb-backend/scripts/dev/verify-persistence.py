#!/usr/bin/env python3
"""Check development persistence through the real backend, including container recreation."""
import base64
import importlib.util
import json
from pathlib import Path
import subprocess
import time
import urllib.request

spec = importlib.util.spec_from_file_location('dev', Path(__file__).with_name('cmdb-dev.py'))
dev = importlib.util.module_from_spec(spec)
spec.loader.exec_module(dev)
evidence = dev.ROOT/'docs/evidence/cmdb24/persistent-http'
evidence.mkdir(parents=True, exist_ok=True)
config, _ = dev.configuration()
base = 'http://127.0.0.1:'+str(config['backend_port'])+'/cmdb-backend/recursos/'
auth = 'Basic '+base64.b64encode(('devadmin:'+(dev.PRIVATE/'app-password').read_text().strip()).encode()).decode()
result = {'pass': False, 'profile': 'dev', 'database': 'cmdb_dev', 'backend_port': config['backend_port']}
process = None
log = None
created = False
logs = []


def request(method, path, data=None, authenticated=False):
    headers = {'Accept':'application/json', 'Content-Type':'application/json'}
    if authenticated:
        headers['Authorization'] = auth
    req = urllib.request.Request(base+path, data=json.dumps(data).encode() if data is not None else None,
                                 headers=headers, method=method)
    with urllib.request.urlopen(req, timeout=15) as response:
        assert response.headers.get('Content-Type','').startswith('application/json')
        return json.load(response)


def stop():
    global process, log
    if process:
        process.terminate()
        try: process.wait(timeout=15)
        except subprocess.TimeoutExpired:
            process.kill(); process.wait(timeout=5)
        process = None
    if log: log.close(); log = None


def start(number):
    global process, log
    p = evidence/('backend-'+str(number)+'.log')
    logs.append(p)
    log = p.open('w')
    process = subprocess.Popen(['python3','scripts/dev/cmdb-dev.py','backend'], cwd=dev.ROOT,
                               stdout=log, stderr=subprocess.STDOUT)
    for _ in range(450):
        if process.poll() is not None:
            raise RuntimeError('Backend exited; see sanitized evidence')
        try:
            r = request('GET','departamento')
            if r['codigo'] == 4: return
        except Exception: pass
        time.sleep(0.1)
    raise RuntimeError('Backend readiness timed out')


try:
    before = json.loads(dev.database('snapshot'))
    start(1)
    rows = request('GET','departamento')['retorno']
    assert not any(x['nombre']=='CMDB24-PERSISTENCE-MARKER' for x in rows), 'Marker already exists; inspect manually'
    assert request('POST','login',authenticated=True)['codigo'] == 1
    r = request('POST','departamento',{'nombre':'CMDB24-PERSISTENCE-MARKER','descripcion':'Synthetic restart verification'},True)
    assert r['codigo']==5
    marker = r['retorno']['id']
    created = True
    assert request('GET','departamento?id='+str(marker))['retorno'][0]['nombre']=='CMDB24-PERSISTENCE-MARKER'
    result['write_and_read_via_backend'] = True
    stop()
    # Recreate container/network without deleting the dedicated volume, stronger than docker restart.
    dev.compose('down')
    dev.up()
    start(2)
    r = request('GET','departamento?id='+str(marker))
    assert r['codigo']==4 and r['retorno'][0]['nombre']=='CMDB24-PERSISTENCE-MARKER'
    result['marker_survived_container_recreation'] = True
    assert request('POST','login',authenticated=True)['codigo'] == 1
    assert request('DELETE','departamento',{'id':marker},True)['codigo']==9
    stop()
    dev.database('marker-remove')
    created = False
    after = json.loads(dev.database('snapshot'))
    assert before == after
    result['marker_removed'] = True
    result['counts_and_schema_restored'] = True
    result['database_inspection'] = json.loads(dev.database('inspect'))
    result['pass'] = True
finally:
    stop()
    if created: dev.database('marker-remove')
    leaked = False
    for p in logs:
        text = p.read_text()
        for name in ['sa-password','db-password','app-password']:
            value = (dev.PRIVATE/name).read_text().strip()
            if value in text: leaked = True
            text = text.replace(value,'[REDACTED]')
        p.write_text(text)
    result['secret_values_in_backend_logs'] = leaked
    result['backend_stopped'] = process is None
    if leaked: result['pass'] = False
    (evidence/'results.json').write_text(json.dumps(result,indent=2))
    if leaked: raise RuntimeError('Credential leak detected; evidence redacted')
print('Persistent development database: HTTP write/read, recreation, reread and marker cleanup passed')
