
require 'json'

CONF = JSON.parse(File.open('mgmt.json').read, {:symbolize_names => true})

module Mgmt
  module Env
    def self.env_v
	ARGV[0]
    end

    def self.prod?
	env_v.eql?('prod')
    end

    def self.development? 
	env_v.eql?('dev')
    end

    def self.conf(k)
      CONF[env_v.to_sym][k] 	
    end
  end
end
