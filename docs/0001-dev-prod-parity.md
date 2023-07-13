# 1. Dev/prod parity

Date: 2023-07-13

## Status

Accepted

## Context

A goal I have for this project is to spend as little on standing infra as I possibly can.
The reasons for this are:

1. There's a good chance I'll get bored with photography and end up not using the thing that I build. I've gotten bored with lots of things, so my current enthusiasm levels aren't the best guide to my future enthusiasm levels. Abandoning the hobby doesn't mean I'll remember to run `terraform destroy`, so I'd like not to spend a thousand dollars on infra I really keep meaning to delete.
2. As long as I'm the only person using it, there will be literally 0 utilization the overwhelming majority of the time. One user is almost the same as zero users.

This points toward a serverless deployment, since scaling down to 0 is something that serverful deployments can't really do.

However! This causes an issue for dev/prod parity. It's _hard_-ish to get serverless things running locally, including both Aurora and API Gateway endpoints.

## Decision

I'll tolerate _some_ inconsistency to avoid other unpleasant choices.

I considered getting things up and running with Localstack. In the Localstack version of striving for dev/prod parity, I would:

* target Terraform at Localstack instead of real AWS
* use the Localstack RDS instance I'd get and the API Gateway deployment for running the backend locally
* still run the frontend with `yarn`, since "somewhere else" (my GH pages deployment of the frontend, `yarn`) I think counts for "parity" whether or not it's the same "somewhere else"

The downsides to going this route are:

1. Localstack Pro is [required for _any_ RDS usage](https://docs.localstack.cloud/references/coverage/coverage_rds/) (both Aurora and plain RDS)
2. Localstack community commits me to [API Gateway v1](https://docs.localstack.cloud/references/coverage/coverage_apigateway/) instead of [API Gateway v2](https://docs.localstack.cloud/references/coverage/coverage_apigatewayv2/). I don't know that I want v2, but I'd like to have the option?
3. The feedback cycle is a little longer for local server changes, since I have to re-bundle things and `terraform apply` them to Localstack to see the difference

Instead, I'll try to maintain parity via abstraction boundaries. These are --

* There's a database _somewhere_, and where it lives is determined by application config read at startup
* The core logic of the application exists as library code which can be implemented _either_ by a serverless handler or by an in-process handler (as currently exists with `narcissusLocal` and `narcissusLambda`)

## Consequences

This will make testing the infra code more difficult. However, given the current setup, relying on config and the logic/handler abstraction boundary is already pretty easy. To make infra testing easier, I should prioritize getting CD set up for the backend as well (it already exists as GH Pages for the frontend).
