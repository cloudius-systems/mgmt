
require 'minitest/autorun'
require 'mgmt'

class TestManager < MiniTest::Unit::TestCase
  def setup
    ARGV[0] = 'dev'
    @manager = Mgmt::AppManager.instance
  end

  def test_listing
    # assert_equal "OHAI!", @meme.i_can_has_cheezburger?
    @manager.list

  end

end
