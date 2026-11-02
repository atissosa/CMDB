#!/usr/bin/env python3
"""Compare Surefire's actual test cases with the preserved legacy inventory."""
import collections
import json
from pathlib import Path
import sys
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
reports = Path(sys.argv[1]) if len(sys.argv) > 1 else root / 'target/surefire-reports'
inventory = json.loads((root / 'docs/cmdb9-inventory.json').read_text())
base_methods = [(item['package'] + '.' + item['class'], method)
                for item in inventory for method in item['test_methods']]
base_methods.append(('com.atissosa.cmdb_backend.CmdbBackendApplicationTests', 'contextLoads'))
additions_file = root / 'docs/test-inventory-additions.json'
additions = json.loads(additions_file.read_text()) if additions_file.exists() else []
added_methods = [(item['package'] + '.' + item['class'], method)
                 for item in additions for method in item['test_methods']]
expected_counts = collections.Counter(base_methods + added_methods)
expected = set(expected_counts)
inventory_duplicates = sorted(key for key, count in expected_counts.items() if count > 1)
actual = collections.Counter()
counts = dict(tests=0, failures=0, errors=0, skipped=0)
for report in sorted(reports.glob('TEST-*.xml')):
    suite = ET.parse(report).getroot()
    for case in suite.findall('testcase'):
        actual[(case.get('classname'), case.get('name'))] += 1
        counts['tests'] += 1
        for kind, key in [('failure', 'failures'), ('error', 'errors'), ('skipped', 'skipped')]:
            counts[key] += case.find(kind) is not None
result = dict(expected_methods=len(expected), base_methods=len(base_methods),
              added_methods=len(added_methods), inventory_duplicates=inventory_duplicates, **counts,
              missing=sorted(expected - actual.keys()),
              duplicates=sorted(key for key, value in actual.items() if value > 1),
              unexpected=sorted(actual.keys() - expected))
print(json.dumps(result, ensure_ascii=False, indent=2))
sys.exit(1 if inventory_duplicates or result['missing'] or result['duplicates'] or result['unexpected']
         or any(counts[key] for key in ['failures', 'errors', 'skipped']) else 0)
