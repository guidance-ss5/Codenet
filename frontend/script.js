// Initialize Swiper
const swiper = new Swiper('.swiper', {
  slidesPerView: 1,
  
  loop: true,

  // Autoplay settings
  autoplay: {
    delay: 4000, 
    disableOnInteraction: false, 
  },

  // Configuration for pagination
  pagination: {
    el: '.slideshow .swiper-pagination',
    clickable: true, 
  },

  // Configuration for navigation buttons
  navigation: {
    nextEl: '.slideshow .swiper-button-next',
    prevEl: '.slideshow .swiper-button-prev',
  },
});