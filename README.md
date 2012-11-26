# Cotton

[![Build Status](https://secure.travis-ci.org/sam/cotton.png?branch=master)](https://next.travis-ci.org/sam/cotton)

JRuby Web Application Framework

## Development

If you'd like to clone the framework itself and play around with it, follow these steps:

#### Install Doubleshot

```bash
gem install doubleshot
```

#### Clone Cotton

```bash
git clone git://github.com/sam/cotton.git
cd cotton
```

#### Run the built-in server

```bash
bin/cotton server
```

#### Examples

Now you can make some example requests demonstrating the application dispatcher, static file serving, and welcome pages (`index.html`):

```bash
$ curl http://localhost:9292
Hello World!

$ curl http://localhost:9292/
Hello World!

$ curl http://localhost:9292/hello_world.txt
Hello Static World!

$ curl http://localhost:9292/welcome
<!DOCTYPE html>
<html>
  <head>
    <title>Welcome Example</title>
  </head>
  <body>
    <h1>Hello Static World!</h1>
  </body>
</html>

$ curl http://localhost:9292/welcome/
<!DOCTYPE html>
<html>
  <head>
    <title>Welcome Example</title>
  </head>
  <body>
    <h1>Hello Static World!</h1>
  </body>
</html>
```