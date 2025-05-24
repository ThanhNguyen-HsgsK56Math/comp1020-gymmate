(function ($) {
	
	"use strict";

	$(function() {
        $("#tabs").tabs();
        $("#goal-tabs").tabs();
    });

	$(window).scroll(function() {
	  var scroll = $(window).scrollTop();
	  var box = $('.header-text').height();
	  var header = $('header').height();

	  if (scroll >= box - header) {
	    $("header").addClass("background-header");
	  } else {
	    $("header").removeClass("background-header");
	  }
	});
	

	$('.schedule-filter li').on('click', function() {
        var tsfilter = $(this).data('tsfilter');
        $('.schedule-filter li').removeClass('active');
        $(this).addClass('active');
        if (tsfilter == 'all') {
            $('.schedule-table').removeClass('filtering');
            $('.ts-item').removeClass('show');
        } else {
            $('.schedule-table').addClass('filtering');
        }
        $('.ts-item').each(function() {
            $(this).removeClass('show');
            if ($(this).data('tsmeta') == tsfilter) {
                $(this).addClass('show');
            }
        });
    });


	// Window Resize Mobile Menu Fix
	mobileNav();


	// Scroll animation init
	window.sr = new scrollReveal();
	

	// Menu Dropdown Toggle
	if($('.menu-trigger').length){
		$(".menu-trigger").on('click', function() {	
			$(this).toggleClass('active');
			$('.header-area .nav').slideToggle(200);
		});
	}


	$(document).ready(function () {
	    $(document).on("scroll", onScroll);
	    
	    //smoothscroll
	    $('.scroll-to-section a[href^="#"]').on('click', function (e) {
	        e.preventDefault();
	        $(document).off("scroll");
	        
	        $('a').each(function () {
	            $(this).removeClass('active');
	        })
	        $(this).addClass('active');
	      
	        var target = this.hash,
	        menu = target;
	       	var target = $(this.hash);
	        $('html, body').stop().animate({
	            scrollTop: (target.offset().top) + 1
	        }, 500, 'swing', function () {
	            window.location.hash = target;
	            $(document).on("scroll", onScroll);
	        });
	    });
	});

	function onScroll(event){
	    var scrollPos = $(document).scrollTop();
	    $('.nav a').each(function () {
	        var currLink = $(this);
	        var href = currLink.attr("href");
	        
	        // Skip if href is not a hash link or is a relative path
	        if (!href || href.startsWith('../') || href.startsWith('./') || !href.startsWith('#')) {
	            return true; // continue to next iteration
	        }
	        
	        var refElement = $(href);
	        if (refElement && refElement.length) {
	            try {
	                var refPosition = refElement.position();
	                if (refPosition && refPosition.top <= scrollPos && refPosition.top + refElement.height() > scrollPos) {
	                    $('.nav ul li a').removeClass("active");
	                    currLink.addClass("active");
	                } else {
	                    currLink.removeClass("active");
	                }
	            } catch (e) {
	                console.log('Error calculating position:', e);
	                currLink.removeClass("active");
	            }
	        } else {
	            currLink.removeClass("active");
	        }
	    });
	}


	// Page loading animation
	 $(window).on('load', function() {

        $('#js-preloader').addClass('loaded');

    });


	// Window Resize Mobile Menu Fix
	$(window).on('resize', function() {
		mobileNav();
	});


	// Window Resize Mobile Menu Fix
	function mobileNav() {
		var width = $(window).width();
		$('.submenu').on('click', function() {
			if(width < 767) {
				$('.submenu ul').removeClass('active');
				$(this).find('ul').toggleClass('active');
			}
		});
	}


	// Goal Selection Functionality
	document.addEventListener('DOMContentLoaded', function() {
	    const goalCards = document.querySelectorAll('.goal-card');
	    let selectedGoal = null;

	    goalCards.forEach(card => {
	        card.addEventListener('click', function() {
	            // Remove selected class from previously selected card
	            if (selectedGoal) {
	                selectedGoal.classList.remove('selected');
	            }

	            // If clicking the same card, deselect it
	            if (selectedGoal === this) {
	                selectedGoal = null;
	            } else {
	                // Select the new card
	                this.classList.add('selected');
	                selectedGoal = this;

	                // Get the selected goal
	                const goal = this.dataset.goal;
	                
	                // You can store the selected goal or trigger other actions here
	                console.log('Selected goal:', goal);
	            }
	        });

	        // Add hover effect sound
	        card.addEventListener('mouseenter', function() {
	            if (typeof Audio !== 'undefined') {
	                const hoverSound = new Audio('assets/sounds/hover.mp3');
	                hoverSound.volume = 0.2;
	                hoverSound.play().catch(() => {});
	            }
	        });
	    });
	});

})(window.jQuery);