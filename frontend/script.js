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

<<<<<<< HEAD
const counters = document.querySelectorAll('.stat-number');
let started = false;

function isInViewport(elem) {
  const rect = elem.getBoundingClientRect();
  return rect.top < window.innerHeight && rect.bottom >= 0;
}

function animateCounters() {
  if (!started && isInViewport(counters[0])) {
    counters.forEach(counter => {
      const target = +counter.getAttribute('data-target');
      let count = 0;
      const step = Math.ceil(target / 100);

      const update = () => {
        if (count < target) {
          count += step;
          if (count > target) count = target;
          counter.innerText = count;
          setTimeout(update, 20);
        }
      };

      update();
    });
    started = true;
  }
}

window.addEventListener('scroll', animateCounters);

const scrollElements = document.querySelectorAll('.scroll-animate');

const observer = new IntersectionObserver((entries) => {
  entries.forEach(entry => {
    if (entry.isIntersecting) {
      entry.target.classList.add('show');
    }
  });
}, {
  threshold: 0.1
});

scrollElements.forEach(el => observer.observe(el));
=======
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
>>>>>>> d9cef9b5728e50e4860fcf05cd6038f43bfc2c61
