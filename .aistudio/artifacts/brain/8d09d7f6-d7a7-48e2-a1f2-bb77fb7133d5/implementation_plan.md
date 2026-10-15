# CPDV Default Translation, Background Voice Reader & Webpage Protection

Comprehensive implementation plan to finalize the Biblia Sacra Catholic Bible app for public release by setting the Catholic Public Domain Version (CPDV) as the default translation, implementing background voice playback with Web Audio and Media Session notification controls, and securing the web app against browser "Save Page As" actions.

## User Review & Critical Decisions

> [!IMPORTANT]
> The architectural direction for the two clarified requirements has been aligned based on user selections:
> 1. **Offline & Webpage Saving Prevention**: Block keyboard shortcuts (Ctrl+S, Cmd+S, Ctrl+P, Cmd+P) and intercept the right-click context menu ("Save page as...") across the application interface.
> 2. **Minimized Voice Playback**: Implement a Web Audio background keeper (`AudioContext` silence pulse) paired with the Media Session API and optional Notification actions, allowing audio to continue playing and be controlled when minimized or on the lock screen.

---

### Confirmed Decisions
- **Default Translation**: Catholic Public Domain Version (`cpdv`) replaces Douay-Rheims (`drb`) as the primary out-of-the-box translation across reading panes, dropdown initializers, quote generators, and default settings fallback.
- **Save Prevention**: Client-side event interception preventing `Ctrl+S`, `Cmd+S`, `Ctrl+P`, `Cmd+P`, and mouse right-click context menu triggers.
- **Background Voice Reader**: An active Web Audio engine running alongside `speechSynthesis` and `navigator.mediaSession` to prevent mobile OS task suspension when the browser or PWA is backgrounded.

---

## 1. Overview & Core Concept

- **What It Does**: Equips users with an immediate, high-fidelity Catholic reading experience starting directly with the modern English Catholic Public Domain Version (CPDV). Enables continuous hands-free Scripture listening that persists seamlessly when switching tabs, locking the device, or minimizing the PWA. Secures the web client against unauthorized local saving.
- **Target Audience / Persona**: Devout Catholic readers, scholars, prayer groups, and commuters listening to daily Scripture readings on mobile phones and desktop computers.
- **Key Value**: Reliable, uninterrupted audio Scripture playback on the lock screen, effortless first-time reading in modern English, and a tamper-resistant web application ready for public release.

---

## 2. User Experience & Visual Design

- **Primary Reading Experience**:
  - First-time visitors and unauthenticated users immediately see the **Catholic Public Domain Version (CPDV - 73 Books)** selected in the header and book selection bar.
  - The reading header prominently reflects "Catholic Public Domain Version" without requiring manual configuration.
- **Voice Player & Media Session**:
  - When the user starts voice playback, the bottom audio bar docks smoothly with full controls (Play/Pause, Next/Previous Verse, Speed, and Voice selector).
  - When the screen is locked or the browser is minimized, the system media notification / lockscreen player displays the book name, chapter, verse number, and audio controls.
  - Lockscreen / notification controls (Play, Pause, Skip Next Verse, Skip Previous Verse) control the playback position directly.
- **Save Prevention Micro-Interactions**:
  - When a user triggers `Ctrl+S` or `Cmd+S`, a clean toast notification appears: *"Webpage saving is disabled."*
  - Context menu clicks are cleanly intercepted on reading content without visual jarring.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: CPDV Default Across All Components**
  - *Chosen Approach*: Update `index.html` (default selected attributes), `app.js` dropdown generator `updateTranslationDropdowns()`, quote tool fallback, and initial state objects.
  - *Why*: Guarantees that both cold starts, anonymous users, and settings resets immediately default to CPDV without breaking existing stored user preferences.
  - *Alternatives Considered*: Forcing CPDV over existing user localStorage — rejected because existing users with custom preferences should retain their selected translations unless they reset their settings.

- **Decision 2: Web Audio API Background Keeper for SpeechSynthesis**
  - *Chosen Approach*: Combine an `AudioContext` with an inaudible periodic oscillator/gain node and a recurring `speechSynthesis.resume()` heartbeat during `visibilitychange` events.
  - *Why*: iOS Safari and Android Chrome aggressively suspend `window.speechSynthesis` within 10–15 seconds of tab backgrounding unless a recognized Web Audio stream or active Media Session pipeline is actively running in the audio stack.
  - *Alternatives Considered*: Silent HTML `<audio loop>` tag alone — often suspended by iOS power management; Web Audio `AudioContext` maintains the audio session much more reliably.

- **Decision 3: Non-Destructive Save Prevention**
  - *Chosen Approach*: Event interception on `keydown` (`Ctrl+S`, `Cmd+S`, `Ctrl+P`, `Cmd+P`) and `contextmenu` (right click), plus blocking image drag events.
  - *Why*: Meets the user's explicit preference while avoiding heavy server-side token gates that could degrade reading speed and SEO.

---

## 4. Technical Architecture & Component Structure

```
┌─────────────────────────────────────────────────────────────┐
│                       Browser / PWA                         │
├──────────────────────────────┬──────────────────────────────┤
│       UI & Read Layer        │      Background Engine       │
│  • Default Translation: CPDV │  • Web Audio Keeper (Pulse)  │
│  • Anti-Save Interceptors    │  • Media Session Controller  │
│    (Ctrl+S, Right-Click)     │  • Speech Heartbeat Keeper   │
└──────────────┬───────────────┴──────────────┬───────────────┘
               │                              │
               ▼                              ▼
┌──────────────────────────────┐ ┌────────────────────────────┐
│      Bible Data Source       │ │   OS Lock Screen & Tray    │
│  • CPDV 73-Book JSON Stream  │ │  • Lockscreen Title/Verse  │
│  • Local IndexedDB Cache     │ │  • Prev/Next/Pause/Play    │
└──────────────────────────────┘ └────────────────────────────┘
```

### Components & Modules Modified

1. **`index.html`**:
   - Set `<option value="cpdv" selected>` as default in translation selectors.
   - Sync metadata and descriptions for public release readiness.

2. **`app.js`**:
   - Update `updateTranslationDropdowns()`: set default fallback to `'cpdv'`.
   - Update quote tool and initial state fallbacks to `'cpdv'`.
   - Enhance `voiceReader` with Web Audio `AudioContext` keeper and `speechSynthesis.resume()` background interval.
   - Add keyboard shortcut handler for `Ctrl+S`, `Cmd+S`, `Ctrl+P`, `Cmd+P` with informational toast.
   - Add `contextmenu` prevention handler on document body.

3. **`server.js` & `package.json`**:
   - Verify deployment endpoints (`/`, `/healthz`, port handling) for Cloud Run stability.
