# AI-Powered Visualization Insights Feature - Implementation Plan

## 🎯 Hackathon MVP (Phase 1)

### Overview

Build an AI assistant that explains Agora visualizations to both researchers and non-researchers. Users click a button on any chart, select their audience type (Simple/Researcher), and see the explanation in a side panel.

### User Flow

1. User views a visualization (e.g., RNA Differential Expression chart)
2. User clicks the AI assistant button on the chart
3. Menu appears with options: "Simple Explanation" | "Researcher Insights"
4. User selects their preferred explanation type
5. Right-side panel opens (or updates if already open) with:
   - Chart screenshot thumbnail
   - AI-generated explanation
   - Loading state while processing

### Implementation Steps

#### 1. Install Dependencies

```bash
npm install html-to-image
```

**Note:** The codebase already has `dom-to-image-more` installed, but we'll use `html-to-image` for better TypeScript support and modern API.

---

#### 2. Create OpenRouter API Service

**Location:** `libs/agora/services/src/lib/openrouter-api.service.ts`

**Interface:**

```typescript
interface VisualizationContext {
  geneSymbol: string;
  ensemblId: string;
  geneName: string;
  chartType: string;
  imageBase64: string;
  scores?: {
    targetRiskScore?: number;
    geneticRiskScore?: number;
    multiOmicScore?: number;
  };
  currentFilters?: Record<string, any>;
  additionalData?: any;
}

interface AIResponse {
  explanation: string;
  timestamp: Date;
  context: VisualizationContext;
}
```

**Service Methods:**

- `explainVisualization(context: VisualizationContext, audienceType: 'simple' | 'researcher'): Observable<AIResponse>`
- Configure OpenRouter endpoint: `https://openrouter.ai/api/v1/chat/completions`
- Model recommendation: `anthropic/claude-sonnet-4.5`
- Handle API errors with user-friendly messages
- Return structured response with explanation text

**Prompt Templates:**

- **Simple:** "Explain this visualization in simple terms for a non-researcher. Use analogies, avoid jargon, and focus on what it means in plain language."
- **Researcher:** "Provide detailed scientific insights from this visualization including: percentile rankings, comparative analysis, distribution characteristics, evidence strength, and research prioritization recommendations."

**Configuration:**

- Add to `apps/agora/app/src/config/config.json`:

```json
{
  "openRouterApiKey": "OPENROUTER_API_KEY",
  "openRouterApiUrl": "https://openrouter.ai/api/v1/chat/completions"
}
```

**Export:**

- Add to `libs/agora/services/src/index.ts`

---

#### 3. Create AI Insights Panel Service (State Management)

**Location:** `libs/agora/services/src/lib/ai-insights-panel.service.ts`

**Purpose:** Manages panel state across components

**Properties:**

```typescript
- isPanelOpen$: BehaviorSubject<boolean>
- currentExplanation$: BehaviorSubject<AIResponse | null>
- isLoading$: BehaviorSubject<boolean>
- explanationHistory$: BehaviorSubject<AIResponse[]>
```

**Methods:**

```typescript
- openPanel(response: AIResponse): void
- closePanel(): void
- updateExplanation(response: AIResponse): void
- clearHistory(): void
```

**Behavior:**

- When panel is already open and user requests new explanation:
  - Keep panel open
  - Show loading state in panel
  - Replace current content with new explanation
  - Add previous explanation to history (accessible via dropdown/tab)

---

#### 4. Create AI Insights Side Panel Component

**Location:** `libs/agora/ui/src/lib/components/ai-insights-panel/`

**Files:**

- `ai-insights-panel.component.ts`
- `ai-insights-panel.component.html`
- `ai-insights-panel.component.scss`

**UI Structure:**

```
┌─────────────────────────────────────┐
│  AI Insights              [X]       │
├─────────────────────────────────────┤
│  [Thumbnail of chart]               │
│                                     │
│  Chart: RNA Differential Expression │
│  Gene: APOE (ENSG00000130203)      │
│                                     │
│  ┌──────────────────────────────┐  │
│  │  Explanation text here...    │  │
│  │  • Point 1                   │  │
│  │  • Point 2                   │  │
│  │  ...                         │  │
│  └──────────────────────────────┘  │
│                                     │
│  [Copy] [Share] [View History]     │
└─────────────────────────────────────┘
```

**Components Used:**

- **PrimeNG Sidebar** (`p-sidebar`):

  - Position: `right`
  - Width: `500px` (desktop), `90%` (mobile)
  - Modal: `false` (allows interaction with main page)
  - `blockScroll`: `false`

- **Content Components:**
  - `mat-card` - explanation container
  - `p-skeleton` - loading state
  - `p-button` - action buttons
  - `mat-icon` - icons (close, copy, history)
  - Custom markdown renderer for explanation text (use existing `marked` library)

**Features:**

- Smooth slide-in animation
- Responsive design
- Scrollable content area
- Sticky header with close button
- Chart thumbnail (small version of screenshot)
- Explanation history dropdown (shows last 5 explanations)
- Copy to clipboard functionality
- Loading skeleton while fetching

**State Management:**

- Subscribe to `AiInsightsPanelService.currentExplanation$`
- Subscribe to `AiInsightsPanelService.isLoading$`
- Subscribe to `AiInsightsPanelService.isPanelOpen$`

---

#### 5. Create AI Assistant Button Component

**Location:** `libs/agora/ui/src/lib/components/ai-assistant-button/`

**Files:**

- `ai-assistant-button.component.ts`
- `ai-assistant-button.component.html`
- `ai-assistant-button.component.scss`

**Visual Design:**

```
┌──────────────────┐
│  ✨ Ask AI   ▼  │  <- Button with dropdown icon
└──────────────────┘
       │
       └──> Menu opens:
            ┌─────────────────────┐
            │ Simple Explanation  │
            │ Researcher Insights │
            └─────────────────────┘
```

**Components Used:**

- **PrimeNG SplitButton** (`p-splitButton`) - perfect for this use case!
  - Main button action: defaults to "Simple Explanation"
  - Dropdown menu with both options
  - Material icon: `psychology` or `smart_toy`

**Inputs:**

```typescript
@Input() targetElement!: HTMLElement;  // Chart element to screenshot
@Input() context!: Partial<VisualizationContext>;  // Chart context
@Input() chartType!: string;  // e.g., "RNA Differential Expression"
```

**Behavior:**

1. User clicks menu item
2. Component captures screenshot using `html-to-image`
3. Builds complete context object
4. Calls `OpenRouterApiService.explainVisualization()`
5. Updates `AiInsightsPanelService` with response
6. Panel opens automatically (or updates if already open)
7. Shows loading state during processing
8. Shows toast notification on error

**Styling:**

- Floating appearance
- Subtle shadow
- Position: absolute, top-right of parent container
- Hover effect with slight scale
- Disabled state while loading

---

#### 6. Integrate with Gene Evidence RNA Component

**Location:** `libs/agora/genes/src/lib/components/gene-evidence-rna/gene-evidence-rna.component.ts`

**Charts to Add AI Button:**

1. Median Expression (line ~26)
2. Differential Expression (line ~83)
3. Consistency of Change RNA (line ~125)
4. Consistency of Change Protein (line ~161)

**Template Changes:**

```html
<!-- Example for Median Expression chart -->
<div class="chart-container" style="position: relative;">
  <div #rnaOEChart>
    <!-- existing chart content -->
  </div>

  <!-- Add AI Assistant Button -->
  <agora-ai-assistant-button
    [targetElement]="rnaOEChart"
    [context]="{
      geneSymbol: gene.hgnc_symbol,
      ensemblId: gene.ensembl_gene_id,
      geneName: gene.name,
      additionalData: {
        medianExpression: medianExpression,
        tissues: ['brain regions shown']
      }
    }"
    chartType="RNA Median Expression"
  />

  <!-- Existing download button -->
  <explorers-download-dom-image ...> </explorers-download-dom-image>
</div>
```

**Context Data to Include:**

- Gene info: `gene.hgnc_symbol`, `gene.ensembl_gene_id`, `gene.name`
- Chart-specific data: `medianExpression`, `differentialExpression`, etc.
- Current filters: Use `HelperService.getUrlParam()` for model/tissue
- Risk scores (if on overview section)

---

#### 7. Add AI Insights Panel to App Root

**Location:** `apps/agora/app/src/app/app.component.html`

**Add panel component:**

```html
<router-outlet />

<!-- AI Insights Panel - Global -->
<agora-ai-insights-panel />
```

This ensures the panel is available on all pages and persists across navigation.

---

#### 8. Update Module Exports and Imports

**libs/agora/ui/src/index.ts:**

```typescript
export * from './lib/components/ai-assistant-button/ai-assistant-button.component';
export * from './lib/components/ai-insights-panel/ai-insights-panel.component';
```

**libs/agora/services/src/index.ts:**

```typescript
export * from './lib/openrouter-api.service';
export * from './lib/ai-insights-panel.service';
```

---

#### 9. Screenshot Capture Implementation

**Using html-to-image:**

```typescript
import { toPng } from 'html-to-image';

async captureChart(element: HTMLElement): Promise<string> {
  try {
    // Configure options for best quality
    const dataUrl = await toPng(element, {
      quality: 1.0,
      pixelRatio: 2, // 2x for retina displays
      backgroundColor: '#ffffff'
    });

    // Remove data URL prefix for API
    return dataUrl.split(',')[1]; // Returns base64 string
  } catch (error) {
    console.error('Failed to capture chart:', error);
    throw new Error('Failed to capture visualization');
  }
}
```

**Alternative Reference:** Check existing implementation in:

- `libs/explorers/ui/src/lib/components/download-dom-image/download-dom-image.component.ts`
- Uses `dom-to-image-more` but similar pattern

---

#### 10. Error Handling & Loading States

**Toast Notifications (PrimeNG MessageService):**

```typescript
// Success
this.messageService.add({
  severity: 'success',
  summary: 'AI Explanation Generated',
  detail: 'View insights in the side panel',
});

// Error
this.messageService.add({
  severity: 'error',
  summary: 'Failed to Generate Explanation',
  detail: 'Please try again or contact support',
});

// Info (while loading)
this.messageService.add({
  severity: 'info',
  summary: 'Generating Explanation',
  detail: 'This may take a few seconds...',
});
```

**Loading States:**

- Button: Show spinner, disable interactions
- Panel: Show skeleton loaders for content
- Chart: Optional overlay with "Capturing..."

---

## 🚀 Enhanced UI/UX (Phase 2 - Future Enhancements)

### 1. Advanced Panel Features

#### Tabbed Interface

Add tabs within the panel:

- **Explanation** (current view)
- **History** (last 10 explanations)
- **Ask Follow-up** (mini chatbot)

Components: `p-tabView` (PrimeNG)

#### Rich Formatting

- Markdown rendering with syntax highlighting
- Collapsible sections for detailed insights
- Data tables for quantitative comparisons
- Components: `mat-expansion-panel`, `p-table`

#### Export Options

- Download as PDF
- Copy as formatted text
- Share via email (mailto link)
- Save to favorites

---

### 2. Comparison Mode

**Feature:** Select multiple charts and get comparative analysis

**UI:**

- Checkbox mode on AI buttons
- "Compare Selected (3)" action button
- Panel shows side-by-side or sequential explanations
- Highlight differences and patterns across visualizations

**Use Case:**
"Compare RNA vs Protein evidence for this gene"

---

### 3. Interactive Insights

**Clickable Highlights:**

- Click on specific data points mentioned in explanation
- Panel highlights corresponding chart elements
- Bidirectional interaction

**Progressive Disclosure:**

- Start with summary (3-4 sentences)
- "Tell me more" buttons expand specific sections
- "Simplify this" button for complex terms

---

### 4. Personalization

**User Preferences:**

- Remember audience type preference (simple/researcher)
- Adjust detail level (brief/moderate/detailed)
- Save favorite explanations
- Export history

**Settings Panel:**

- Location: User profile menu
- Toggle features: auto-open panel, show notifications
- Choose AI model (if multiple available)

---

### 5. Visual Enhancements

**Panel Animations:**

- Smooth slide-in with spring physics
- Fade transitions between explanations
- Shimmer effect on loading

**Button Styles:**

- Gradient background with AI theme
- Animated sparkle icon
- Pulse effect to draw attention (first time user)

**Chart Integration:**

- Highlight chart elements mentioned in explanation
- Add annotation layer
- Interactive tooltips linking to explanation sections

---

## 💬 Companion Chatbot (Phase 3)

### Overview

A persistent AI assistant that users can chat with across all Agora pages, building on the visualization insights feature.

### Difficulty Assessment

**Complexity:** Medium (2-3 days)

**Easy Parts (40%):**

- UI widget creation (floating button + dialog)
- Basic chat interface (message list + input)
- Service state management
- Message history persistence

**Medium Parts (40%):**

- Context gathering across different pages
- Conversation threading
- Integration with existing explanation system
- File/screenshot attachments

**Complex Parts (20%):**

- Context-aware responses (understanding current page)
- Cost management (token usage)
- Conversation summarization (long chats)
- Multi-turn reasoning about visualizations

---

### Architecture

#### 1. AI Chat Service

**Location:** `libs/agora/services/src/lib/ai-chat.service.ts`

**State:**

```typescript
interface ChatMessage {
  id: string;
  role: 'user' | 'assistant' | 'system';
  content: string;
  timestamp: Date;
  attachments?: {
    type: 'screenshot' | 'visualization-context';
    data: any;
  }[];
}

interface ChatSession {
  id: string;
  messages: ChatMessage[];
  context: {
    currentPage: string;
    currentGene?: string;
    recentVisualizations?: VisualizationContext[];
  };
}
```

**Properties:**

```typescript
- currentSession$: BehaviorSubject<ChatSession>
- messages$: Observable<ChatMessage[]>
- isTyping$: BehaviorSubject<boolean>
- isOpen$: BehaviorSubject<boolean>
```

**Methods:**

```typescript
- sendMessage(content: string, attachments?: any[]): Observable<ChatMessage>
- attachVisualization(context: VisualizationContext): void
- clearHistory(): void
- loadSession(sessionId: string): void
- saveSession(): void
```

**Persistence:**

- Store in `localStorage` for page refresh survival
- Expire sessions after 7 days
- Limit to last 50 messages per session

---

#### 2. Floating Chat Widget

**Location:** `libs/agora/ui/src/lib/components/ai-chat-widget/`

**Visual Design:**

**Minimized State:**

```
                    ┌────────┐
                    │  💬 AI │  <- Floating button
                    └────────┘
                    (bottom-right)
```

**Expanded State:**

```
┌─────────────────────────────────┐
│  AI Assistant           [−] [X] │
├─────────────────────────────────┤
│  ┌───────────────────────────┐ │
│  │ Assistant: Hi! I can help │ │
│  │ explain visualizations... │ │
│  └───────────────────────────┘ │
│                                 │
│  ┌───────────────────────────┐ │
│  │ You: What does this chart │ │
│  │ mean?                     │ │
│  └───────────────────────────┘ │
│                                 │
│  [Screenshot shown]            │
│                                 │
│  ┌───────────────────────────┐ │
│  │ Assistant: This chart...  │ │
│  │ [typing indicator]        │ │
│  └───────────────────────────┘ │
├─────────────────────────────────┤
│  [📎] Type your question... [→]│
└─────────────────────────────────┘
```

**Components:**

- **Trigger:** `mat-fab` (Material Floating Action Button)
- **Dialog:** `p-dialog` (PrimeNG) or custom overlay
- **Messages:** Custom component with virtual scrolling
- **Input:** `p-inputGroup` with `p-button`
- **Attachments:** `p-fileUpload` (for screenshots)

**Features:**

- Minimize/maximize toggle
- Draggable (optional)
- Resize height (optional)
- Unread message badge
- Quick actions: "Explain this page", "Attach screenshot"

---

#### 3. Context-Aware Features

**Auto-Context Injection:**

```typescript
// Automatically add system message with page context
{
  role: 'system',
  content: `
    Current page: Gene Details - APOE
    User is viewing: RNA Differential Expression
    Available data: Target Risk Score: 4.68, Genetic Risk: 2.85
    Recent interaction: User viewed "Simple Explanation" of this chart
  `
}
```

**Smart Suggestions:**

- When user lands on gene page: "Would you like me to explain this gene?"
- After viewing explanation: "Any questions about this visualization?"
- On comparison page: "I can help compare these genes"

**Cross-Page Memory:**

- Service tracks route changes
- Maintains conversation across navigation
- References previous pages: "Like the chart we discussed on the APOE page..."

---

#### 4. Integration with Visualization Insights

**Seamless Connection:**

1. User requests explanation via button → Panel opens
2. Chat widget shows: "I just explained this chart. Any questions?"
3. User can continue conversation in chat
4. Chat can reference the explanation: "As I mentioned in the panel..."

**Shared Context:**
Both systems use same `VisualizationContext` interface

**Quick Actions in Chat:**

- "📊 Attach current chart" button
- "📋 Show explanation in panel" link
- "🔄 Compare with another chart" option

---

#### 5. Cost Management

**Token Optimization:**

- Limit context window to last 10 messages
- Summarize older conversation history
- Compress visualization context (don't send full screenshots repeatedly)
- Cache common explanations

**Usage Tracking:**

```typescript
interface UsageMetrics {
  tokensUsed: number;
  requestCount: number;
  costEstimate: number;
}
```

**User Feedback:**

- Show usage stats in settings
- Warning when approaching limits
- Option to clear history to reset

---

#### 6. Advanced Chat Features

**Multi-turn Reasoning:**

```
User: What's the target risk score for this gene?
AI: The target risk score is 4.68

User: How does that compare to other genes?
AI: That's in the top 15% of all genes...

User: Should I prioritize this for research?
AI: Yes, because... [considers previous context]
```

**Conversation Branching:**

- Save multiple conversation threads
- Switch between topics
- Resume old conversations

**Proactive Assistance:**

- Detect user confusion (e.g., rapid page changes)
- Offer help: "You seem to be searching for something. Can I help?"

---

### Implementation Phases

#### Phase 3.1: Basic Chat (1 day)

- Create chat widget UI
- Implement AI chat service
- Basic message send/receive
- Message history storage

#### Phase 3.2: Context Awareness (1 day)

- Page tracking
- Auto-context injection
- Integration with visualization insights
- Smart suggestions

#### Phase 3.3: Advanced Features (0.5 day)

- Screenshot attachments
- Usage tracking
- Polish animations
- Error recovery

---

## 📋 Technical Considerations

### Security

- **API Key Management:** Store in environment config, never in source code
- **Rate Limiting:** Implement client-side throttling
- **Input Validation:** Sanitize user inputs before sending to AI
- **CORS:** Configure OpenRouter requests properly

### Performance

- **Screenshot Optimization:**
  - Limit resolution to 1920px width max
  - Use JPEG for large charts (smaller file size)
  - Cache screenshots (don't recapture same chart)
- **Lazy Loading:** Load AI components only when needed
- **Debouncing:** Prevent rapid-fire requests

### Accessibility

- **Keyboard Navigation:** All buttons/panels accessible via keyboard
- **Screen Readers:** Proper ARIA labels
- **Focus Management:** Trap focus in panel when open
- **High Contrast:** Support high contrast mode

### Mobile Responsiveness

- **Panel:** Full-width on mobile, slide from bottom
- **Button:** Larger touch targets (44px min)
- **Chat Widget:** Fixed position, avoid covering content

### Error Recovery

- **Network Failures:** Retry logic with exponential backoff
- **API Errors:** User-friendly messages
- **Timeout Handling:** Cancel long-running requests
- **Fallback:** Graceful degradation if AI unavailable

---

## 🎨 Design Specifications

### Colors

- **AI Theme:** Purple/blue gradient (#667EEA to #764BA2)
- **Success:** #10B981 (green)
- **Error:** #EF4444 (red)
- **Loading:** #3B82F6 (blue)

### Typography

- **Explanation Text:** 16px, line-height 1.6
- **Headings:** 18px bold
- **Code Snippets:** Monospace, 14px

### Spacing

- **Panel Padding:** 24px
- **Button Margin:** 12px from chart edge
- **Message Spacing:** 16px between messages

### Animation Timings

- **Panel Slide:** 300ms ease-out
- **Button Hover:** 150ms ease-in-out
- **Loading Pulse:** 1500ms infinite

---

## 🧪 Testing Strategy

### Unit Tests

- Service methods (API calls, state management)
- Context object building
- Error handling

### Integration Tests

- Button → Service → Panel flow
- Screenshot capture
- Panel state updates

### E2E Tests

- Complete user flow: click button → select type → view explanation
- Panel persistence across navigation
- Multiple explanations in sequence

### Manual Testing Checklist

- [ ] All 4 RNA charts have working buttons
- [ ] Both explanation types work (simple/researcher)
- [ ] Panel opens/updates correctly
- [ ] Loading states display properly
- [ ] Error messages are user-friendly
- [ ] Works on mobile devices
- [ ] Keyboard navigation works
- [ ] Screenshot quality is good
- [ ] API responses are relevant

---

## 📦 Deliverables (Hackathon)

### Must Have

- ✅ OpenRouter API service
- ✅ AI assistant button with menu
- ✅ Right-side insights panel
- ✅ Screenshot capture
- ✅ Context object builder
- ✅ Integration with 1-2 charts (RNA section)
- ✅ Basic error handling
- ✅ Console logging of responses

### Nice to Have

- 🎯 All 4 RNA charts integrated
- 🎯 Explanation history
- 🎯 Copy to clipboard
- 🎯 Toast notifications
- 🎯 Loading skeleton states

### Future Work (Post-Hackathon)

- 🔮 Integration with all gene page sections
- 🔮 Comparison mode
- 🔮 Chatbot widget
- 🔮 User preferences
- 🔮 Export features

---

## 🚀 Getting Started

### Prerequisites

1. OpenRouter API account and key
2. Node.js and npm installed
3. Nx CLI for monorepo commands

### Development Workflow

```bash
# Install dependencies
npm install

# Run Agora app in dev mode
nx serve agora

# Run tests
nx test agora

# Build for production
nx build agora --prod
```

### Configuration

1. Add OpenRouter API key to `apps/agora/app/src/config/config.json`
2. Set feature flag: `"aiInsightsEnabled": true`
3. Configure CORS if needed

---

## 📚 Resources

### API Documentation

- **OpenRouter:** https://openrouter.ai/docs
- **Claude API:** https://docs.anthropic.com/claude/reference/messages_post
- **html-to-image:** https://github.com/bubkoo/html-to-image

### UI Libraries

- **PrimeNG:** https://primeng.org/
- **Angular Material:** https://material.angular.io/

### Agora Codebase

- **Services:** `libs/agora/services/src/lib/`
- **UI Components:** `libs/agora/ui/src/lib/components/`
- **Gene Components:** `libs/agora/genes/src/lib/components/`

---

## 🤝 Questions & Next Steps

### Open Questions

1. Should we support multiple AI models (GPT-4, Claude, etc.)?
2. Do we need user authentication for API key per-user?
3. Should explanations be cached to reduce API costs?
4. What's the character limit for explanations?

### After Hackathon Review

- Gather user feedback on explanation quality
- Measure usage metrics (clicks, panel opens)
- Evaluate API costs
- Prioritize Phase 2 features based on feedback

---

**Last Updated:** 2025-12-11
**Authors:** Development Team
**Status:** Ready for Implementation 🚀
