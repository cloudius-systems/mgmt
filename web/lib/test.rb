require 'sinatra'
require 'haml'

get '/' do
  @pg = "index"
  haml :index
end

get '/:pg' do |p|
  @pg = p
  haml :index
end
