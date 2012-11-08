class Cotton::CLI::Commands::Server < Cotton::CLI

  def self.summary
    <<-EOS.margin
      Start a web server for your project.
    EOS
  end

  def self.options
    Options.new do |options|
      options.banner = "Usage: cotton server"
      options.separator ""
      options.separator "Options"

      options.port = 9292
      options.on "-p", "--port [PORT_NUMBER]", "Start the server on a non-standard port (default is 9292)." do |port|
        options.port = port
      end

      options.separator ""
      options.separator "Summary: #{summary}"
    end
  end

  def self.start(args)
    options = self.options.parse!(args)
    require "doubleshot/setup"
    org.sam.cotton.Cotton.start options.port

    return 0
  end
end