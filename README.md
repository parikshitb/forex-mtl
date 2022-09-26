### forex-mtl
---
#### Paidy Interview: Scala assignment
---
This is a work related to the [assignment](https://github.com/paidy/interview/blob/master/Forex.md) given.

#### How to run
- Open the code in either VSCode or IntelliJ IDEA (Maybe it supports other editors as well but not tested)
- For VSCode, I have used the extension [metals](https://scalameta.org/metals/docs/editors/vscode/)
- For IntellijIDEA, open the `build.sbt` file as a project
- Basically this project is runnable as sbt project.

#### What is accomplished
Since I had no prior knowledge of Scala, it was a steep learning curve. After the much learning about Scala, Sbt, Cats, Cats.Effect etc. I was able to finish only part of the assignment. 
The first part where we need to call one-frame API using http client to get the exchange rate is accomplished.
I tried to create a commit history to resemble the process I followed during development.

Please do go through the code.

#### What about remaining work
The next challenge given is supporting 10000 requests from our service when API limitation is 1000 requests.
Though I could not write any code for this, I'd like to share my solution theory for this.

Since we want to limit the requests to one-frame to 1000, It is not possible to call one-frame every time user requests us for an exchange rate. Therefore synchronous communication is not possible, we will have to do this asynchronously and it will require caching or some sort of data storage on our side.

Based on one-frame documentation[https://hub.docker.com/r/paidyinc/one-frame] we can pass n number of currency pairs to get exchange rates for all of them. That means we can get exchange rate for n number of pairs with single request. So, for our solution, call one-frame service every x minutes (x < 5) to fetch data of all currencies at once and cache/store them. When user requests our service we will serve the request from our cache/storage. Moreover, such a function, which polls one-frame, need not be part of our code. It can be a separate funciton/job deployed elsewhere. 

#### Challenges
If we want to support all the currencies supported [here](https://www.xe.com/iso4217.php), the number of pairs become huge. Supported currencies are 162 and with combination of every other currency(and back) it will be 162 x 161 = 26082 pairs. Since the API is GET API, usually services don't support this insanely long requests.
This request length can be reduced further by dividing the currency pairs into different requests and doing frequent polling.
To show some calculations, let's assume that we need to call one-frame API at least once every 5 minutes. (No latency or other factors)
Initially, 
1 request with 26082 pairs every 5 minutes; This will add up to only 288 requests per day. But sine the API supports 1000 requests we can accomodate some more.
To come near to consume full quota of 1000 requests per day, we can send 1000 / 24 =~41 requests per hour.
To keep data not more than 5 minutes old, we can divide the pairs into 41 % 12 = 3 requests every 5 minutes.
So, each request can contain 26082 / 3 = 8694 pairs.
Next?
Well, if we have conversion rate from USD to JPY, we don't need from JPY to USD and we can reduce this to 4347 pairs.
This still is a long request and that remains to be a challenge.



