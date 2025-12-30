import type React from "react"
import { Layers3 } from "lucide-react"
import { ThemeToggle } from "@/components/theme-toggle"

export function Header({ children }: { children?: React.ReactNode }) {
  return (
    <header className="border-b border-border bg-card">
      <div className="flex h-16 items-center gap-4 px-6">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-lg bg-accent">
            <Layers3 className="h-5 w-5 text-accent-foreground" />
          </div>
          <div>
            <h1 className="text-lg font-semibold tracking-tight">KanbanFlow</h1>
            <p className="text-xs text-muted-foreground">Project Management</p>
          </div>
        </div>

        <div className="flex-1 flex items-center justify-center">{children}</div>

        <div className="flex items-center gap-2">
          <ThemeToggle />
        </div>
      </div>
    </header>
  )
}
