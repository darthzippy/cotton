Cotton
======

[![Build Status](https://secure.travis-ci.org/sam/cotton.png?branch=master)](https://next.travis-ci.org/sam/cotton)

JRuby Web Application Framework

Development
=====

If you'd like to clone the framework itself and play around with it, follow these steps:

Install Doubleshot
===

```bash
gem install doubleshot
```

Clone Cotton
===

```bash
git clone git://github.com/sam/cotton.git
cd cotton
```

Run the built-in server
===

```bash
bin/cotton server
```

Examples
===

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

All these examples hover around 5,000 requests-per-second with same-machine console logging disabled using `httperf`. For now. This should be sped up drastically once response caching is implemented for `PublicFileHandler`. Most requests for static files that are requested multiple times will be served directly from memory then, and the `PublicFileHandler` will also cache "misses" so that requests for dynamic content can effectively short-circuit the `PublicFileHandler` for the most requested routes.

These caches will be flushed by a file system watcher whenever the static file system is modified.

```bash
$ httperf --num-conns=50 --num-calls=2000 --port 9292 --uri=/welcome
httperf --client=0/1 --server=localhost --port=9292 --uri=/welcome --send-buffer=4096 --recv-buffer=16384 --num-conns=50 --num-calls=2000
httperf: warning: open file limit > FD_SETSIZE; limiting max. # of open files to FD_SETSIZE
Maximum connect burst length: 1

Total: connections 50 requests 100000 replies 100000 test-duration 19.968 s

Connection rate: 2.5 conn/s (399.4 ms/conn, <=1 concurrent connections)
Connection time [ms]: min 358.8 avg 399.4 max 665.9 median 366.5 stddev 81.9
Connection time [ms]: connect 0.1
Connection length [replies/conn]: 2000.000

Request rate: 5007.9 req/s (0.2 ms/req)
Request size [B]: 69.0

Reply rate [replies/s]: min 4876.1 avg 5025.4 max 5105.4 stddev 129.4 (3 samples)
Reply time [ms]: response 0.2 transfer 0.0
Reply size [B]: header 143.0 content 129.0 footer 0.0 (total 272.0)
Reply status: 1xx=0 2xx=100000 3xx=0 4xx=0 5xx=0

CPU time [s]: user 2.76 system 16.45 (user 13.8% system 82.4% total 96.2%)
Net I/O: 1667.7 KB/s (13.7*10^6 bps)

Errors: total 0 client-timo 0 socket-timo 0 conn refused 0 conn reset 0
Errors: fd-unavail 0 addrunavail 0 ftab-full 0 other 0
```