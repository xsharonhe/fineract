# Birthday Bank Assignment

`curl -u mifos:password -k -X GET https://localhost:8443/fineract-provider/api/v1/savingsaccounts?birthday=12-01&tenantIdentifier=default` where birthday is `MM-dd` format

## Approach to Designing API layer
To enable the ability to query savings accounts by the client's birthday, I chose to introduce a birthday query parameter to the existing endpoint at `/v1/savingsaccount`, as a string with a `MM-dd` format. Adding this parameter should be a similar filter in the database, but we want to take into consideration a data validation layer to ensure that the parameter is formatted correctly.

## Testing
Since I did not add any new logic pieces, I decided not to add any unit tests and focus on integration tests.

For integration tests:
* Parameterized test for a variety of invalid cases for the birthday parameter
* Single creating savings account to ensure it changes total accounts, accounts by the client's birthday, but does not change other birthdays 
* Batch creating savings accounts where the clients have the same birthday, but are born different / same years

## Further Steps
I wanted this code change to be isolated to querying the savings account by a client's birthday, but here are potential changes I would make in the future:
* A lot of the integration tests in `ClientSavingsIntegrationTest` are repetitive for creating a client, product savings, activating a savings account etc. I would group that flow into one function 
* I would approach integration tests differently, since I noticed all the savings account being created in the integration tests remained even after test cleanup. This can bulk the state of the local database, and I believe should remain the same before / after testing.
