require 'digest/md5'
require 'java'
java_import 'net.lingala.zip4j.core.ZipFile'

module Mgmt
  class DeployUnit

   attr_accessor :filename      

   def initialize(filename)
     @filename = filename
   end

   def extract
     source = "#{Mgmt::Env.conf(:uploads)}/#{@filename}"
     zipFile = ZipFile.new(source)  
     md5 = Digest::MD5.file(source)
     dest = "#{Mgmt::Env.conf(:apps)}/#{md5}"
     zipFile.extractAll(dest)
   end
  end
end
