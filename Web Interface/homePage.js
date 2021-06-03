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
    var pageCount = $(".line-content").length / 8;

    for (var i = 0; i < pageCount; i++) {

        $("#pagin").append('<li><a href="#">' + (i + 1) + '</a></li> ');
    }
    $("#pagin li").first().find("a").addClass("current")
    showPage = function (page) {
        $(".line-content").hide();
        $(".line-content").each(function (n) {
            console.log(n);
            if (n >= 8 * (page - 1) && n < 8 * page)
                $(this).show();
        });
    }

    showPage(1);

    $("#pagin li a").click(function () {
        $("#pagin li a").removeClass("current");
        $(this).addClass("current");
        showPage(parseInt($(this).text()))
    });
  /*  $(".SearchBar").focusout(function () {
        $(".searchBtn").fadeOut(1000, function () {
            $(".SearchBar").hide(1000, function () {
                $(".searchBtn1").show(1000);
            });
            
        });
    })*/

});