module Mgmt
  class App
    module Views
	class Manage < Layout
	  def apps
	    [{:name => "foo", :state => "running", :action => "stop"}]
	  end
	end
    end
  end
end
