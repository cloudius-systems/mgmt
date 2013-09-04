require 'jmx4r'
module Mgmt
  class App
    module Views
	class Jmx < Layout
	  def permgen
	    JMX::MBean.establish_connection(:host => "localhost", :port => 3000)
	    memory = JMX::MBean.find_by_name 'java.lang:type=MemoryPool,name=PS Perm Gen'
	    %w(committed used init).map do |v|
		{:name => "#{v}" , :value => memory.usage.send(v)}
	    end
	  end
	end
    end
  end
end
