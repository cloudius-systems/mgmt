from osv.modules.api import *
from osv.modules.filemap import FileMap
from osv.modules import api

usr_files = FileMap()
usr_files.add('${OSV_BASE}/mgmt/crash/target/dependencies').to('/usr/mgmt/lib')

_crash_jar = '/usr/mgmt/crash-1.0.0.jar'
_cloudius_jar = '/java/cloudius.jar'
_logging_opts = ['-Djava.util.logging.config.file=/usr/mgmt/config/logging.properties']

shell = run_java(
        classpath=[
            _cloudius_jar,
            _crash_jar,
            '/usr/mgmt/lib/*'
        ],
        args=_logging_opts + [
            '-jar', _crash_jar
        ])

# Some CLI commands depend on httpserver presence
_httpserver_module = require('httpserver')

httpserver = _httpserver_module.default
_osvinit_module = require('osvinit')
osvinit = _osvinit_module.default

full = [
    shell,
    httpserver,
    osvinit,
]

default = full
