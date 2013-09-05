require 'json'
require 'pathname'
require 'singleton'
java_import 'RunJava'

STATE_TO_ACT = {:running => :stop, :stopped => :start}

module Mgmt
  class AppManager
     include Singleton

    def initialize
      @state = {}
    end

    def list
      apps = Pathname.new(Mgmt::Env.conf(:apps)).children.select { |c| c.directory? }
      apps.map do |p| 
	  s = @state[p.basename]
	  s ||= :stopped
	  json = JSON.parse(Pathname::glob("#{p}/*.json").first.read)
	  json.merge({:state => s ,:action => STATE_TO_ACT[s], :id => p.basename})
	end
    end

    def start(id)
      path = "#{Mgmt::Env.conf(:apps)}/#{id}"
      file = Pathname::glob("#{path}/*.json").first.read
      args = JSON.parse(file,{:symbolize_names => true})[:args]
    	 RunJava.new(path).parseArgs(args.split(' '))
      @state[id] = :running 
    end

    def stop(id)
	@state[id] = :stopped
    end
  end
end
