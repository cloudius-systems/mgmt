require 'rubygems'
require 'bundler'

Bundler.require

require "test.rb"

set :run, false
set :environment, :production

run Sinatra::Application

