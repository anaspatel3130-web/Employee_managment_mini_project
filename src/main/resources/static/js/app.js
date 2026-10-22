/**
 * Employee Management System - Client Side JavaScript
 * Vanilla JS implementation without any heavy frameworks.
 */

// -----------------------------------------------------------------
// 0. Theme Switcher System (White / Light Modern vs. Cyberpunk Neon Black)
// -----------------------------------------------------------------
function applyTheme(theme) {
  const activeTheme = (theme === 'light') ? 'light' : 'neon-dark';
  document.documentElement.setAttribute('data-theme', activeTheme);
  try {
    localStorage.setItem('apex_theme', activeTheme);
  } catch (e) {
    // Ignore storage issues if any
  }
  updateThemeUi(activeTheme);
}

function updateThemeUi(theme) {
  const btnLight = document.getElementById('btnThemeLight');
  const btnNeon = document.getElementById('btnThemeNeon');
  const badgeLight = document.getElementById('badgeActiveLight');
  const badgeNeon = document.getElementById('badgeActiveNeon');

  if (theme === 'light') {
    if (btnLight) btnLight.className = 'theme-pill-btn active-light';
    if (btnNeon) btnNeon.className = 'theme-pill-btn';

    if (badgeLight) {
      badgeLight.textContent = 'Active';
      badgeLight.style.background = '#dcfce7';
      badgeLight.style.color = '#15803d';
    }
    if (badgeNeon) {
      badgeNeon.textContent = 'Inactive';
      badgeNeon.style.background = '#e2e8f0';
      badgeNeon.style.color = '#475569';
    }
  } else {
    if (btnNeon) btnNeon.className = 'theme-pill-btn active-neon';
    if (btnLight) btnLight.className = 'theme-pill-btn';

    if (badgeLight) {
      badgeLight.textContent = 'Inactive';
      badgeLight.style.background = '#e2e8f0';
      badgeLight.style.color = '#475569';
    }
    if (badgeNeon) {
      badgeNeon.textContent = 'Active';
      badgeNeon.style.background = 'rgba(0, 240, 255, 0.2)';
      badgeNeon.style.color = '#00f0ff';
    }
  }
}

// Global hooks for onclick handlers across templates
window.applyTheme = applyTheme;
window.setAppTheme = applyTheme;

// Immediate theme execution on script load to prevent flash of wrong theme
const initialTheme = (function() {
  try {
    return localStorage.getItem('apex_theme') || 'neon-dark';
  } catch (e) {
    return 'neon-dark';
  }
})();
document.documentElement.setAttribute('data-theme', initialTheme);

document.addEventListener('DOMContentLoaded', () => {

  // Synchronize UI buttons on DOM ready
  updateThemeUi(initialTheme);

  // -----------------------------------------------------------------
  // 1. Delete Confirmation Modal Handler
  // -----------------------------------------------------------------
  const deleteModal = document.getElementById('deleteModal');
  const deleteForm = document.getElementById('deleteForm');
  const deleteEmployeeNameEl = document.getElementById('deleteEmployeeName');
  const cancelDeleteBtn = document.getElementById('cancelDeleteBtn');

  // Attach click listener to all delete buttons across tables
  document.querySelectorAll('.btn-delete-trigger').forEach(btn => {
    btn.addEventListener('click', (e) => {
      e.preventDefault();
      const empId = btn.getAttribute('data-id');
      const empName = btn.getAttribute('data-name');

      if (deleteModal && deleteForm) {
        deleteForm.action = `/employees/delete/${empId}`;
        if (deleteEmployeeNameEl) {
          deleteEmployeeNameEl.textContent = `${empName} (ID: #${empId})`;
        }
        openModal(deleteModal);
      }
    });
  });

  if (cancelDeleteBtn && deleteModal) {
    cancelDeleteBtn.addEventListener('click', () => {
      closeModal(deleteModal);
    });
  }

  // Close modal when clicking outside of modal container
  if (deleteModal) {
    deleteModal.addEventListener('click', (e) => {
      if (e.target === deleteModal) {
        closeModal(deleteModal);
      }
    });
  }

  function openModal(modal) {
    modal.classList.add('active');
    document.body.style.overflow = 'hidden';
  }

  function closeModal(modal) {
    modal.classList.remove('active');
    document.body.style.overflow = '';
  }

  // Escape key closes modal
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && deleteModal && deleteModal.classList.contains('active')) {
      closeModal(deleteModal);
    }
  });


  // -----------------------------------------------------------------
  // 2. Auto-dismiss Flash Alerts
  // -----------------------------------------------------------------
  const alerts = document.querySelectorAll('.alert');
  alerts.forEach(alert => {
    const closeBtn = alert.querySelector('.alert-close');
    if (closeBtn) {
      closeBtn.addEventListener('click', () => {
        dismissAlert(alert);
      });
    }

    // Auto dismiss after 4.5 seconds
    setTimeout(() => {
      dismissAlert(alert);
    }, 4500);
  });

  function dismissAlert(alertEl) {
    alertEl.style.transition = 'opacity 0.4s ease, transform 0.4s ease';
    alertEl.style.opacity = '0';
    alertEl.style.transform = 'translateY(-10px)';
    setTimeout(() => {
      if (alertEl.parentElement) {
        alertEl.remove();
      }
    }, 400);
  }


  // -----------------------------------------------------------------
  // 3. Live Clock in Header
  // -----------------------------------------------------------------
  const liveClockEl = document.getElementById('liveClock');
  if (liveClockEl) {
    function updateClock() {
      const now = new Date();
      const options = {
        weekday: 'short',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      };
      liveClockEl.innerHTML = `<span>🕒</span> ${now.toLocaleDateString('en-US', options)}`;
    }
    updateClock();
    setInterval(updateClock, 1000);
  }


  // -----------------------------------------------------------------
  // 4. Mobile Sidebar Navigation Toggle
  // -----------------------------------------------------------------
  const mobileToggleBtn = document.getElementById('mobileNavToggle');
  const sidebar = document.querySelector('.sidebar');

  if (mobileToggleBtn && sidebar) {
    mobileToggleBtn.addEventListener('click', () => {
      sidebar.classList.toggle('mobile-open');
    });

    document.addEventListener('click', (e) => {
      if (window.innerWidth <= 900 && 
          sidebar.classList.contains('mobile-open') && 
          !sidebar.contains(e.target) && 
          e.target !== mobileToggleBtn) {
        sidebar.classList.remove('mobile-open');
      }
    });
  }


  // -----------------------------------------------------------------
  // 5. Dynamic Avatar Color Palettes
  // -----------------------------------------------------------------
  const avatarColors = [
    '#4f46e5', '#0ea5e9', '#10b981', '#f59e0b',
    '#ec4899', '#8b5cf6', '#14b8a6', '#f97316'
  ];

  document.querySelectorAll('.user-avatar').forEach(avatar => {
    const text = avatar.textContent.trim();
    if (text) {
      let hash = 0;
      for (let i = 0; i < text.length; i++) {
        hash = text.charCodeAt(i) + ((hash << 5) - hash);
      }
      const colorIndex = Math.abs(hash) % avatarColors.length;
      avatar.style.backgroundColor = avatarColors[colorIndex];
    }
  });


  // -----------------------------------------------------------------
  // 6. Automatic Form Submit on Department Filter Change
  // -----------------------------------------------------------------
  const deptFilterSelect = document.getElementById('deptFilterSelect');
  if (deptFilterSelect) {
    deptFilterSelect.addEventListener('change', () => {
      const filterForm = document.getElementById('filterForm');
      if (filterForm) {
        filterForm.submit();
      }
    });
  }

});
