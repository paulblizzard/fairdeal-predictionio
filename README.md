# Fairdeal-PredictionIO template

v2 -- Instead, I am going to do this a bit differently, and create individual prediction IO applications dealing with each question/answer pairing individually.  This seems like a much more straightforward method of dealing with this.  There are many benefits to this approach:  

1)  A user can mark the fairness of each answer individually, which is the most correct way to make this sort of judgment
2)  A user can see individually the feedback for each answer to see why their dealing is not fair.  It's a lot stronger to say that it's not a fair dealing because xx and yy.

v1 -- original version where I attempted to ascertain the fairness of the dealing in its entirety in PredictionIO.  

# Installation instructions

1)  [Install Docker](https://www.docker.com/products/overview) 
2)  [Get the Community PredictionIO Docker image](https://github.com/apache/incubator-predictionio/)
3)  Install the Community Docker image (instructions at the above link)
4)  Clone this repo in the docker image.  You will need to copy this six times (one for each question).
5)  Edit engine.json and update appName
6)  Import the stop words and sample_dealing json files for each engine.
7)  [Install the PredictionIO engines](http://predictionio.incubator.apache.org/start/deploy/).  You will need to do this six times, one for each question.
8) Import some test data (for each of the engines):

x6:
- pio import --appid fairdeal-answer1 --input data/stopwords.json
- pio import --appid fairdeal-answer1 --input data/sample_dealing.json

7)  Test the engines

# Text Classification Engine

PredictionIO has excellent documentation that I recommend you read:

[Text Classification Tutorial](https://docs.prediction.io/demo/textclassification/) has a Quick Start guide and implementation details.

[Event API](http://predictionio.incubator.apache.org/datacollection/eventapi/) has a good guide about how to send Event Data and Query data to PredictionIO through the API.  This is how the Rails application communicates with PredictionIO for fairdeal.

[Handling multiple events](http://predictionio.incubator.apache.org/templates/similarproduct/multi-events-multi-algos/)  was my initial plan for sending a dealing through to PredictionIO and handling everything at once.  This tutorial discusses how the weighting of different factors can be undertaken.

# Release Information

## Version 4.0

Re-structure and design preparator and algo. less memory usage and run time is faster.
Move BIDMach, VW & SPPMI algo changes to `bidmach` branch temporarily.

## Version 3.1

Fix DataSource to read "content", "e-mail", and use label "spam" for tutorial data.
Fix engine.json for default algorithm setting.


## Version 2.2

Modified PreparedData to use MLLib hashing and tf-idf implementations.

## Version 2.1

Fixed dot product implementation in the predict methods to work with batch predict method for evaluation.

## Version 2.0

Included three different data sets: e-mail spam, 20 newsgroups, and the rotten tomatoes semantic analysis set. Includes Multinomial Logistic Regression algorithm for text classification.

## Version 1.2

Fixed import script bug occuring with Python 2.

## Version 1.1 Changes

Changed data import Python script to pull straight from the [20 newsgroups](http://qwone.com/~jason/20Newsgroups/) page.
