$(document).ready(function () {
    $(".searchBtn1").click(function () {
        $(".searchBtn1").hide(1000,function () {
            $(".SearchBar").show(1000, function () {
                $(".searchBtn").show(1000, function () {
                    $(".SearchBar").focus();
                });
            });
        });
    })
  
  /*  $(".SearchBar").focusout(function () {
        $(".searchBtn").fadeOut(1000, function () {
            $(".SearchBar").hide(1000, function () {
                $(".searchBtn1").show(1000);
            });
            
        });
    })*/

});