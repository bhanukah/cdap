$(document).ready(function() {
    "use strict";
    $(".sticky-content-left-side").sticky({
        topSpacing: 0
    }), $(".scroll-section").bind("click", function(e) {
        var t = $(this);
        $("html, body").stop().animate({
            scrollTop: $(t.attr("href")).offset().top - 10
        }, 1500, "easeInOutExpo"), e.preventDefault()
    }), $("#mobile-category-menu .navbar-collapse ul li a").click(function() {
        $("#mobile-category-menu .navbar-toggle:visible").click()
    }), $("#mobileCartToggle").on("click", function(e) {
        return e.preventDefault(), $(".ui-order-online-right-side").toggleClass("cart-visible"), !1
    }), $("body").scrollspy({
        target: ".order-list",
        offset: 20
    }), $(window).on("scroll", function() {
        var e = $(this).scrollTop();
        $("#mobile-category-menu .navbar-brand").html($("#mobile-category-menu li.active a").html()), void 0 != $(".ui-order-online-left-side").offset() && e > $(".ui-order-online-left-side").offset().top ? $("#mobile-category-menu").addClass("visible") : $("#mobile-category-menu").removeClass("visible")
    }), $(window).width() > 992 && $(".sticky-content-right-side").sticky({
        topSpacing: 10
    })
});