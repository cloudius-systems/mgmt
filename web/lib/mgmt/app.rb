require 'rubygems'
require 'sinatra'
require 'mustache/sinatra'
require "sinatra/reloader" if Mgmt::Env.development?
require 'java'
require 'json'


module Mgmt
  class App < Sinatra::Base
    register Mustache::Sinatra
    register Sinatra::Reloader if Mgmt::Env.development?
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

    get '/upload' do
	mustache :upload
    end       

    # Handle POST-request (Receive and save the uploaded file)
    post '/upload' do  
	h = params['files']
	File.open('/uploads/' + h[:filename], 'w') do |dest|
	  dest.write(h[:tempfile].read) 
	end
	res = {"files"=> 
              [ { "name" => h[:filename] , "size"=> 902604, "url"=> "http://example.org/files/picture1.jpg",
			"thumbnailUrl"=> "http://fortawesome.github.io/Font-Awesome/icon/laptop",
			"deleteUrl"=> "http:\/\/example.org\/files\/picture1.jpg", "deleteType"=> "DELETE" }]}

	return res.to_json
    end
  end
end

