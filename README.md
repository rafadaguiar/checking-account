# checking-account
![CircleCI](https://circleci.com/gh/rafadaguiar/checking-account.svg?style=shield&circle-token=45b9344b3bba78e93fa0c72a2351537671b9e080)

A checking account API following functional programming principles.

## How I approached the checking account problem
The first idea that comes to mind when trying to model a checking account is to have a balance variable that gets updated after each operation. To avoid racing conditions I moved away from this idea and implemented a checking account that instead uses a history of operations to track its current state.
Operations are registered to an account as they come, and whenever someone issues a retrieval operation like 'get balance' all the history of operations for that account is then used to compute the balance value. This has some disadvantages like increasing the processing and memory usage for most operations, but it allows for more thread safe operations and also makes the handling of operations a lot more flexible (e.g., say you need to invalidate an operation; all you need to do is to ignore it from computation, there's no need to update balances everywhere).

## Documentation
[API Docs on Swagger](https://swaggerhub.com/apis/rafadaguiar/purple-banker/0.1.0)

## How to test it

### Heroku deployment

The application is already deployed on heroku at [purple-banker](https://purple-banker.herokuapp.com), so you can point your test application directly to this url. Keep in mind that the server will have a cold start.

### Local deployment
You can also deploy this application locally by using the following commands:

```bash
# If you don't have mongodb installed
$ brew install mongo
# if you don't have sbt installed
$ brew install sbt
```
```bash
$ git clone git@github.com:rafadaguiar/checking-account.git
$ cd checking-account
$ sbt run # sever will run on localhost:5000
```
