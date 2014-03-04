from osv.modules.api import *
from osv.modules.filemap import FileMap
from osv.modules import api

usr_files = FileMap()

usr_files.add('${OSV_BASE}/mgmt/httpserver/httpserver.so').to('/usr/mgmt/httpserver.so')
usr_files.add('${OSV_BASE}/mgmt/api').to('/usr/mgmt/api')
usr_files.add('${OSV_BASE}/mgmt/swagger-ui/dist').to('/usr/mgmt/swagger-ui/dist')

_cloudius_jar = '/java/cloudius.jar'
_logging_opts = ['-Djava.util.logging.config.file=/usr/mgmt/config/logging.properties']

shell = run_java(
        classpath=[
            _cloudius_jar
        ],
        args=_logging_opts + [
            '-jar', '/usr/mgmt/crash-1.0.0.jar'
        ])

httpserver = api.run('/usr/mgmt/httpserver.so')

full = [
    shell,
    httpserver,
]

default = full
