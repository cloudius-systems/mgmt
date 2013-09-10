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

    if(Mgmt::Env.prod?)
	set :mustache, { :views => '/usr/mgmt/views', :templates => '/usr/mgmt/templates' }
	set :public_folder, '/usr/mgmt/public'
    else
	set :mustache, { :views => 'views', :templates => 'templates' }
	set :public_folder, 'public'
    end

    configure { set :server, :puma }
    set :bind, '0.0.0.0'
    set :port, 8080

    get '/' do
	mustache :index
    end

    get '/upload' do
	mustache :upload
    end       

    get '/manage' do
	mustache :manage
    end       

    get '/monitor' do
	mustache :jmx
    end       

    def save_file(params)
	h = params['files']
	File.open("#{Mgmt::Env.conf(:uploads)}/#{h[:filename]}", 'w') do |dest|
	  dest.write(h[:tempfile].read) 
	end
	Mgmt::DeployUnit.new(h[:filename]).extract
	h
    end

    # returns a json has for jquery upload plugin
    post '/deploy-interactive' do  
	h = save_file(params)
	size = File.size("#{Mgmt::Env.conf(:uploads)}/#{h[:filename]}")
	file = {'name' => h[:filename], 'size'=> size , 'url'=> '', 'thumbnailUrl'=> '', 'deleteUrl'=> '', 'deleteType'=> '' }
	return {'files'=> [file]}.to_json
    end

    post '/action' do
      AppManager.instance.send(params['action'],params['id'])
	AppManager.instance.app_state(params['id']).to_json
    end
  end
end

