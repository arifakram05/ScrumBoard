'use strict';

angular.module('scrumApp.jira', ['ui.router'])

.config(function ($stateProvider, $urlRouterProvider) {
    $stateProvider
        .state('jira', {
            /*url: '/jira',*/
            templateUrl: "templates/jira/jira.html",
            controller: "jiraCtrl"
        })
})

.factory('jiraService', ['$http', '$q', function ($http, $q) {

    //var JIRA_URI = 'http://127.0.0.1:8080/ScrumBoard/rest/services/jira/';
    var TEST_JIRA_URI = "templates/jira/jira.json";

    //define all factory methods
    var factory = {
        getJiraDetails: getJiraDetails
    };

    return factory;

    function getJiraDetails(jira, associateId) {
        console.log('Retrieving JIRA details for : ', jira, ' ', associateId);
        var deferred = $q.defer();
        $http({
                method: 'GET',
                url: TEST_JIRA_URI
            })
            .then(
                function success(response) {
                    console.log('JIRA details retrieved: ', response);
                    deferred.resolve(response.data);
                },
                function error(errResponse) {
                    console.error('Error while retrieving jira details ', errResponse);
                    deferred.reject(errResponse);
                }
            );
        return deferred.promise;
    }

}])

.controller('jiraCtrl', ['$scope', '$filter', '$q', 'SharedService', 'jiraService', '$mdDialog', function ($scope, $filter, $q, SharedService, jiraService, $mdDialog) {

    console.log('inside jira controller');

    $scope.jiraURL = 'https://jira2.cerner.com/browse/';

    $scope.findJiras = function (jira) {
        console.log('jira details to fetch : ', jira);

        //URI POST call to save the scrum
        var promise = jiraService.getJiraDetails(jira, $scope.loggedInUserId);
        promise.then(function (result) {
                console.log('JIRA details retrieved :', result);
                $scope.jiraDetails = result;
            })
            .catch(function (resError) {
                console.log('Failed to retrieve JIRA details :: ', resError);
                //show failure message to the user
                SharedService.showError(resError.message);
            });
    }

    //alerts to user
    function notifyUser(message) {
        $mdDialog.show(
            $mdDialog.alert()
            .clickOutsideToClose(true)
            .textContent(message)
            .ok('Got it!')
        );
    }

}]);
