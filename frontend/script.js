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
