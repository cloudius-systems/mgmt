require 'sinatra'
require 'haml'
require 'java'


module Mgmt
  class App < Sinatra::Base
    configure { set :server, :puma }
    set :bind, '0.0.0.0'
    set :port, 8080
    set :views, 'views'
    set :public_folder, 'public'

    get '/' do
	haml :index
    end

    get "/upload" do
	haml :upload
    end       

    # Handle POST-request (Receive and save the uploaded file)
    post "/upload" do  
	File.open('/uploads/' + params['myfile'][:filename], "w") do |f|
	  f.write(params['myfile'][:tempfile].read) 
	end
	return "The file was successfully uploaded!"
    end
  end
end

