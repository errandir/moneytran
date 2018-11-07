This is a money transfer REST-service simulator

* build with `./gradlew build`
* unpack distribution from `./build/distributions/` to some `<target>` directory
* run with `<target>/moneytran-*/bin/moneytran`

---

The application starts at `7000` port with five sample accounts:

ID | name   | money amount
-- | ------ | ------------
1  | Alice  | 100
2  | Bob    | 200
3  | Cooper | 300
4  | Daniel | 400
5  | Elsa   | 500

---

## REST API

### POST /transaction

Prepares transaction.

* Reqest body: `{ "src": <source-account-id>, "dst": <destination-account-id>, "amount": <amount-of-money-to-tansmit> }`
* Response body: `<id-of-new-transaction>` to be commited or `-1` on failure.

### POST /transaction/&lt;id&gt;

Commits transaction.

* Response body: `1` if transaction was successfully comited by this or any previous request or `0` otherwise.



