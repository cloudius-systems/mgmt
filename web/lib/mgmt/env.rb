
module Mgmt
  module Env
    def self.prod?
	ARGV[0].eql?('prod')
    end

    def self.development? 
	ARGV[0].eql?('dev')
    end
  end
end
