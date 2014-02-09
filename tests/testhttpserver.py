#!/usr/bin/env python
import json
import sys
import re
import glob
import argparse
import os
import subprocess
import time
import threading
import urllib2
import urllib
import unittest
import re
import socket

parser = argparse.ArgumentParser(description="""Testing the Httpserver""")

parser.add_argument('--connect', help='Connect to an existing image', action='store_true')
parser.add_argument('--run_script', help='path to the run image script', default='scripts/run.py')
parser.add_argument('--cmd', help='the command to execute', default='"/usr/mgmt/httpserver.so"')
parser.add_argument('--use_sudo', help='Use sudo with -n option instead of port forwarding', action='store_true')
parser.add_argument('--jsondir', help='location of the json files', default='mgmt/api/listings/')
parser.add_argument('--port', help='set the port number', type=int,
                    default=8000)
parser.add_argument('--ip', help='set the ip address',
                    default='localhost')

config = parser.parse_args()
class test_httpserver(unittest.TestCase):
    @classmethod
    def get_url(cls, uri):
        return 'http://' + config.ip + ':' + str(config.port) + uri

    @classmethod
    def get_json_api(cls, name):
        json_data = open(config.jsondir + name)
        data = json.load(json_data)
        json_data.close()
        return data

    def assert_between(self, msg, low, high, val):
        self.assertGreaterEqual(val, low, msg=msg)
        self.assertLessEqual(val, high, msg=msg)

    @classmethod
    def get_api(cls, api_definition, nickname):
        for api in api_definition["apis"]:
            if api["operations"][0]["nickname"] == nickname:
                return api
        return None

    @classmethod
    def path_by_nick(cls, api_definition, nickname):
        api = cls.get_api(api_definition, nickname)
        return api["path"]

    @classmethod
    def is_reachable(cls):
        s = socket.socket()
        try:
            s.connect((config.ip, config.port))
            s.close()
            return True
        except socket.error:
            return False

    def validate_path(self, api_definition, nickname, value):
        path = self.path_by_nick(api_definition, nickname)
        self.assertEqual(value, self.curl(path))

    def test_os_version(self):
        path = self.path_by_nick(self.os_api, "getOSversion")
        self.assertRegexpMatches(self.curl(path), "v0" , path)

    def test_manufactor(self):
        self.validate_path(self.os_api, "getOSmanufacturer", "cloudius-systems")

    def test_os_uptime(self):
        path = self.path_by_nick(self.os_api, "getLastBootUpTime")
        up_time = self.curl(path)
        time.sleep(2)
        self.assert_between(path, up_time + 1, up_time + 3, self.curl(path))

    def test_os_date(self):
        path = self.path_by_nick(self.os_api, "getDate")
        val = self.curl(path).encode('ascii', 'ignore')
        self.assertRegexpMatches(val, "... ...  \d+ \d\d:\d\d:\d\d 20..", path)


    def test_os_total_memory(self):
        path = self.path_by_nick(self.os_api, "getTotalVirtualMemorySize")
        val = self.curl(path)
        self.assertGreater(val, 1024 * 1024 * 256, msg= " Memory should be greater than 256Mb ")

    def test_os_free_memory(self):
        path = self.path_by_nick(self.os_api, "getFreeVirtualMemory")
        val = self.curl(path)
        self.assertGreater(val, 1024 * 1024 * 256, msg=" Free memory should be greater than 256Mb ")

    @classmethod
    def curl(cls, api, post=False):
        url = cls.get_url(api)
        if post:
            data = urllib.urlencode({'Fake' : 'data-to-become-post'})
            req = urllib2.Request(url, data)
            response = urllib2.urlopen(req)
        else:
            response = urllib2.urlopen(url)
        return json.load(response)

    @classmethod
    def exec_os(cls):
        if config.use_sudo:
            return subprocess.Popen(["/usr/bin/sudo", config.run_script, "-n", "-e", config.cmd])
        return subprocess.Popen([config.run_script, "--forward", "tcp:" + str(config.port) + "::" + str(config.port), "-e", config.cmd])

    @classmethod
    def shutdown(cls):
        path = cls.path_by_nick(cls.os_api, "shutdown")
        try:
            cls.curl(path, True)
        except Exception as e:
            None

        cls.os_process.poll()
        if cls.os_process.returncode == None:
            cls.os_process.terminate()

    @classmethod
    def setUpClass(cls):
        if not config.connect:
            cls.os_process = cls.exec_os()
        cls.os_api = cls.get_json_api("os.json")
        retry = 10
        while (not cls.is_reachable()):
            time.sleep(1)
            retry -= 1
            if (retry == 0):
                cls.shutdown()
                raise Exception("Server is down")

    @classmethod
    def tearDownClass(cls):
        cls.shutdown()

if __name__ == '__main__':
    unittest.main()
