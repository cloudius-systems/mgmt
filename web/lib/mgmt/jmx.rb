module Mgmt
  class Jmx
    def start_server
     java_import com.j256.simplejmx.server.JmxServer 
     jmxServer = JmxServer.new(3000)
     jmxServer.start
    end
  end
end
