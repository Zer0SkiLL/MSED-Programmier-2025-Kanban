"use client"

import { useState, useEffect } from "react"
import { KanbanBoard } from "@/components/kanban-board"
import { Header } from "@/components/header"
import { BoardSelector } from "@/components/board-selector"
import { boardsApi, columnsApi, tasksApi, type Board, type Column, type Task } from "@/lib/api"

export default function Page() {
  const [boards, setBoards] = useState<Board[]>([])
  const [currentBoardId, setCurrentBoardId] = useState<string>("")
  const [currentBoard, setCurrentBoard] = useState<Board | null>(null)
  const [loading, setLoading] = useState(true)
  const [boardLoading, setBoardLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  // Fetch boards on component mount
  useEffect(() => {
    const fetchBoards = async () => {
      try {
        setLoading(true)
        const fetchedBoards = await boardsApi.getAll()
        setBoards(fetchedBoards)
        if (fetchedBoards.length > 0) {
          setCurrentBoardId(fetchedBoards[0].id)
        }
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch boards")
        console.error("Error fetching boards:", err)
      } finally {
        setLoading(false)
      }
    }

    fetchBoards()
  }, [])

  // Fetch board details (columns and tasks) when board changes
  useEffect(() => {
    const fetchBoardDetails = async () => {
      if (!currentBoardId) return

      try {
        setBoardLoading(true)
        const board = await boardsApi.getById(currentBoardId)
        
        // Fetch columns for this board
        const columns = await columnsApi.getAll(currentBoardId)
        
        // Fetch tasks for each column
        const columnsWithTasks = await Promise.all(
          columns.map(async (column) => {
            const tasks = await tasksApi.getAll(currentBoardId, column.id)
            return { ...column, tasks }
          })
        )
        
        console.log("Fetched columns with tasks:", columnsWithTasks)
        
        setCurrentBoard({
          ...board,
          columns: columnsWithTasks,
        })
      } catch (err) {
        setError(err instanceof Error ? err.message : "Failed to fetch board details")
        console.error("Error fetching board details:", err)
      } finally {
        setBoardLoading(false)
      }
    }

    fetchBoardDetails()
  }, [currentBoardId])

  const handleBoardChange = (boardId: string) => {
    setCurrentBoardId(boardId)
  }

  const handleAddBoard = async (boardName: string) => {
    try {
      const newBoard = await boardsApi.create({ name: boardName })
      setBoards([...boards, newBoard])
      setCurrentBoardId(newBoard.id)
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to create board")
      console.error("Error creating board:", err)
    }
  }

  const handleBoardUpdate = async (updatedBoard: Board) => {
    try {
      await boardsApi.update(updatedBoard.id, { name: updatedBoard.name })
      setBoards(boards.map((b) => (b.id === updatedBoard.id ? updatedBoard : b)))
      setCurrentBoard(updatedBoard)
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to update board")
      console.error("Error updating board:", err)
    }
  }

  const handleBoardDelete = async (boardId: string) => {
    try {
      await boardsApi.delete(boardId)
      const newBoards = boards.filter((b) => b.id !== boardId)
      setBoards(newBoards)
      if (currentBoardId === boardId && newBoards.length > 0) {
        setCurrentBoardId(newBoards[0].id)
      }
    } catch (err) {
      setError(err instanceof Error ? err.message : "Failed to delete board")
      console.error("Error deleting board:", err)
    }
  }

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading boards...</p>
        </div>
      </div>
    )
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center max-w-md">
          <div className="text-destructive text-6xl mb-4">⚠️</div>
          <h2 className="text-xl font-semibold mb-2">Error Loading Boards</h2>
          <p className="text-muted-foreground mb-4">{error}</p>
          <button
            onClick={() => window.location.reload()}
            className="px-4 py-2 bg-primary text-primary-foreground rounded-md hover:bg-primary/90"
          >
            Retry
          </button>
        </div>
      </div>
    )
  }

  if (boards.length === 0) {
    return (
      <div className="min-h-screen flex flex-col">
        <Header>
          <BoardSelector
            boards={boards}
            currentBoardId={currentBoardId}
            onBoardChange={handleBoardChange}
            onAddBoard={handleAddBoard}
          />
        </Header>
        <main className="flex-1 flex items-center justify-center">
          <div className="text-center max-w-md">
            <div className="text-6xl mb-4">📋</div>
            <h2 className="text-2xl font-semibold mb-2">No Boards Yet</h2>
            <p className="text-muted-foreground mb-6">
              Create your first board to get started with your Kanban board
            </p>
          </div>
        </main>
      </div>
    )
  }

  return (
    <div className="min-h-screen flex flex-col">
      <Header>
        <BoardSelector
          boards={boards}
          currentBoardId={currentBoardId}
          onBoardChange={handleBoardChange}
          onAddBoard={handleAddBoard}
        />
      </Header>
      <main className="flex-1 overflow-hidden">
        {boardLoading ? (
          <div className="h-full flex items-center justify-center">
            <div className="text-center">
              <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-primary mx-auto mb-4"></div>
              <p className="text-muted-foreground">Loading board...</p>
            </div>
          </div>
        ) : currentBoard ? (
          <KanbanBoard
            board={currentBoard}
            onBoardChange={handleBoardUpdate}
          />
        ) : null}
      </main>
    </div>
  )
}
