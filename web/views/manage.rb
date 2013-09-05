
module Mgmt
  class App
    module Views
	class Manage < Layout
	  def apps
         Mgmt::AppManager.instance.list
	  end
	end
    end
  end
end
