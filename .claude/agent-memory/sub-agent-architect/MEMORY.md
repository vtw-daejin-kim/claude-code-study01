# Sub-Agent Architect Memory

## Project State (2026-02-18)
- **Project**: Ecommerce MVP Backend (Spring Boot 3.4.1, Java 17)
- **Current Phase**: Pre-Phase 0 -- only skeleton exists (EcommerceApplication.java, build.gradle, YML configs, docker-compose.yml)
- **All tasks unchecked**: Phase 0-8 entirely pending
- **Package root**: `kr.go.ecommerce`
- **Domains**: user, brand, product, like, cart, order, stats

## Agent Design Decisions
- 8 agents designed covering: scaffolding, each business domain, testing, and code review
- Agents follow phase dependency order: scaffolding -> user -> brand -> product -> like/cart (parallel) -> order -> stats
- Each agent embeds project-specific patterns: JPA+MyBatis hybrid, ErrorCode/BusinessException, ApiResponse<T>/PageResponse<T>
- Agents reference CLAUDE.md conventions and plan.md file paths

## Key Files
- [Agent configs](./agent-configs.md) -- Full JSON for all 8 agents
- [Project analysis](./project-analysis.md) -- Detailed findings from codebase analysis
