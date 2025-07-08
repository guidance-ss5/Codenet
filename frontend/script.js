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

document.addEventListener('DOMContentLoaded', function() {
  
  // Your existing Swiper initialization code should be here
  const swiper = new Swiper('.slideshow', {
    loop: true,
    pagination: {
      el: '.swiper-pagination',
      clickable: true,
    },
    navigation: {
      nextEl: '.swiper-button-next',
      prevEl: '.swiper-button-prev',
    },
    autoplay: {
      delay: 5000,
      disableOnInteraction: false,
    },
  });

  // --- NEW: Trending Section Scroller Logic ---
  const trendingRow = document.getElementById('trending-row-js');
  const nextButton = document.getElementById('trending-next-btn');

  // Check if these elements exist on the page to avoid errors
  if (trendingRow && nextButton) {
    nextButton.addEventListener('click', () => {
      // Calculate how much to scroll based on the visible width of the row
      const scrollAmount = trendingRow.clientWidth;

      // Check if we are near the end of the scrollable content
      const isAtEnd = trendingRow.scrollLeft + scrollAmount >= trendingRow.scrollWidth - trendingRow.clientWidth;

      if (isAtEnd) {
        // If at the end, scroll back to the beginning for a loop effect
        trendingRow.scrollLeft = 0;
      } else {
        // Otherwise, scroll to the next "page" of items
        trendingRow.scrollLeft += scrollAmount;
      }
    });
  }

});