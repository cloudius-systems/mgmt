require 'rubygems'
require 'sinatra'
require 'mustache/sinatra'
require 'java'


module Mgmt
  class App < Sinatra::Base
    register Mustache::Sinatra
    require 'views/layout'

    set :mustache, {
	:views     => 'views',
	:templates => 'templates'
    }

    configure { set :server, :puma }
    set :bind, '0.0.0.0'
    set :port, 8080
    set :public_folder, 'public'

    get '/' do
	mustache :index
    end

    get "/upload" do
	mustache :upload
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

