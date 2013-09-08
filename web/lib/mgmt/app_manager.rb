require 'json'
require 'pathname'
require 'singleton'

java_import 'org.xeustechnologies.jcl.JarClassLoader'
java_import 'org.xeustechnologies.jcl.JclObjectFactory'
java_import 'java.util.concurrent.TimeUnit'
java_import 'java.util.concurrent.CountDownLatch'

STATE_TO_ACT = {:running => :stop, :stopped => :start}

module Mgmt
  class AppManager
    include Singleton

    # apps is the global running apps tracking store, it contains all running apps:
    # {"439493befe8870fe5f52eb821ce9cf76" => {:state => :running, :instance=> object}}
    def initialize
	@apps = {}
	scan
    end

    def list
	scan
	@apps.map {|id, v| {:id => id}.merge(v) }
    end

    def start(id)
	file = Pathname::glob("#{path(id)}/*.json").first.read
	conf = JSON.parse(file,{:symbolize_names => true})
	launch(id,conf).await(100, TimeUnit::MILLISECONDS)
	@apps[id][:state] = :running 
	if(@apps[id][:instance])
	  @apps[id][:action] = :stop
	else
	  @apps[id][:action] = nil
	end
    end

    def stop(id)
	raise Exception.new('application does not support stop') unless @apps[id][:instance]
	@apps[id][:instance].stop
      @apps[id][:instance] = nil
	@apps[id][:state] = :stopped
	@apps[id][:action] = :start
    end

    def app_state(id)
	{:action => @apps[id][:action], :state =>  @apps[id][:state]}
    end

    private
    def path(id)
	"#{Mgmt::Env.conf(:apps)}/#{id}"
    end

    # scans apps folder and updates @apps
    def scan
	app_dirs = Pathname.new(Mgmt::Env.conf(:apps)).children.select { |c| c.directory?}
	app_dirs.each do |p|
	  json = JSON.parse(Pathname::glob("#{p}/*.json").first.read)
	  unless @apps[p.basename.to_s] 
	    puts p.basename
	    @apps[p.basename.to_s] = json.merge({:state => :stopped ,:action => :start})
	  end
	end 
    end

    def launch(id,conf)
	 latch = CountDownLatch.new(1)
	 Thread.new do
	  jcl = JarClassLoader.new
	  jcl.add(path(id))
	  main = jcl.loadClass(conf[:main])
	  if main.java_instance_methods.map {|m| m.name.to_sym}.include? :run 
	    instance = JclObjectFactory.getInstance.create(jcl,conf[:main])
	    @apps[id][:instance] = instance
	    latch.countDown()
	    instance.run()
	  else # using its main method
	    latch.countDown()
	    f = main.java_class_methods.select{|m| m.name.include?('main')}.first
	    f.invoke(nil,[@apps[id][:args].to_java(:string)].to_java(:object))
	  end
	end
	latch 
    end
  end
end
