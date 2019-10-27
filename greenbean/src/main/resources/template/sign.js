(function(){
    $(document).ready(function () {
        var activeClassName = "active";
        var hideFormClassName = "hideForm";
        var signInTab = $("#signInTab");
        var signUpTab = $("#signUpTab");
        var signInForm = $("#signInForm");
        var signUpForm = $("#signUpForm");

        var signInObject =  {
            tab: signInTab,
            form: signInForm
        }

        var signUpObject =  {
            tab: signUpTab,
            form: signUpForm
        }

        var signObjectArray = [signInObject,signUpObject];

        var handlerDataObject = {
            activeClassName: activeClassName,
            hideFormClassName: hideFormClassName,
            signObjectArray: signObjectArray
        }

        var tabOnClickHandler = function(event) {
            handlerDataObject = event.data;
            var tabDomObject = $(event.target);
            for (var i = 0; i < signObjectArray.length; i++) {
                if(signObjectArray[i].tab.is(tabDomObject)){
                    if(!tabDomObject.hasClass(handlerDataObject.activeClassName)) {
                        tabDomObject.addClass(handlerDataObject.activeClassName);
                        signObjectArray[i].form.removeClass(handlerDataObject.hideFormClassName);
                    }
                }else {
                    signObjectArray[i].tab.removeClass(handlerDataObject.activeClassName);
                    signObjectArray[i].form.addClass(handlerDataObject.hideFormClassName);
                }
            }
        }

        for (var i = 0; i < signObjectArray.length ; i++) {
            signObjectArray[i].tab.click(handlerDataObject,tabOnClickHandler);
        }

        signUpForm.submit(function () {
            debugger;
            var validator = signUpForm.validate({
                rules : {
                    username : {
                        required : true
                    },
                    password : {
                        required : true
                    },
                    confirmPassword : {
                        required : true,
                        equalTo : "#passwordSignUp"
                    }
                }
            });
            var result = validator.valid();
            // validator.destroy();
            return result;
        });

    });
})();