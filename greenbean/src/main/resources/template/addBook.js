(function () {
    $(function(){
        const nextButton = $('#nextButton');
        nextButton.on("click",function(){
            // TODO first step ： 验证ISBN
            $('.d-none').removeClass("d-none");
            $('#isbn').attr('readonly','');
        });
        const cancelButton = $('#cancelButton');
        cancelButton.on('click',function () {
            window.location.href="home";
        });
    });
})();