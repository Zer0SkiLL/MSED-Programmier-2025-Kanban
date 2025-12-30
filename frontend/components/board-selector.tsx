"use client"

import { ChevronDown, Plus } from "lucide-react"
import { Button } from "@/components/ui/button"
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu"
import type { Board } from "@/app/page"
import { useState } from "react"
import { AddBoardDialog } from "./add-board-dialog"

type BoardSelectorProps = {
  boards: Board[]
  currentBoardId: string
  onBoardChange: (boardId: string) => void
  onAddBoard: (boardName: string) => void
}

export function BoardSelector({ boards, currentBoardId, onBoardChange, onAddBoard }: BoardSelectorProps) {
  const currentBoard = boards.find((b) => b.id === currentBoardId)
  const [showAddDialog, setShowAddDialog] = useState(false)

  return (
    <>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="outline" className="gap-2 bg-transparent">
            <span className="font-medium">{currentBoard?.name}</span>
            <ChevronDown className="h-4 w-4 text-muted-foreground" />
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align="center" className="w-56">
          {boards.map((board) => (
            <DropdownMenuItem key={board.id} onClick={() => onBoardChange(board.id)} className="cursor-pointer">
              {board.name}
            </DropdownMenuItem>
          ))}
          <DropdownMenuItem onClick={() => setShowAddDialog(true)} className="cursor-pointer text-primary">
            <Plus className="h-4 w-4 mr-2" />
            Create New Board
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>

      <AddBoardDialog open={showAddDialog} onOpenChange={setShowAddDialog} onAdd={onAddBoard} />
    </>
  )
}
