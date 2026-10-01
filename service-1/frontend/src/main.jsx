import { StrictMode, useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

const API_URL = 'http://localhost:8080/api/greetings'

function App() {
  const [greetings, setGreetings] = useState([])
  const [form, setForm] = useState({ name: '', message: '' })
  const [status, setStatus] = useState('Загрузка записей...')
  const [saving, setSaving] = useState(false)

  const loadGreetings = async () => {
    try {
      const response = await fetch(API_URL)
      if (!response.ok) throw new Error(`Сервер ответил с кодом ${response.status}`)
      setGreetings(await response.json())
      setStatus('')
    } catch (error) {
      setStatus(`Ошибка: ${error.message}. Проверьте, запущен ли Spring Boot 1.`)
    }
  }

  useEffect(() => {
    loadGreetings()
  }, [])

  const submit = async (event) => {
    event.preventDefault()
    setSaving(true)
    setStatus('')
    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(form),
      })
      if (!response.ok) {
        const detail = await response.json().catch(() => null)
        throw new Error(detail?.message || `Сервер ответил с кодом ${response.status}`)
      }
      const saved = await response.json()
      setGreetings((current) => [...current, saved])
      setForm({ name: '', message: '' })
    } catch (error) {
      setStatus(`Ошибка: ${error.message}`)
      await loadGreetings()
    } finally {
      setSaving(false)
    }
  }

  return (
    <main className="container">
      <section className="hero">
        <p className="eyebrow">Микросервис 1 · React + Spring Boot + MySQL</p>
        <h1>Сообщения</h1>
        <p>Сообщение сохраняется в первой базе MySQL и надёжно передаётся сервису 2.</p>
      </section>

      <section className="card">
        <h2>Оставить сообщение</h2>
        <form onSubmit={submit}>
          <label>
            Имя
            <input
              required
              value={form.name}
              onChange={(event) => setForm({ ...form, name: event.target.value })}
              placeholder="Например, Анна"
            />
          </label>
          <label>
            Сообщение
            <textarea
              required
              value={form.message}
              onChange={(event) => setForm({ ...form, message: event.target.value })}
              placeholder="Привет из React!"
              rows="4"
            />
          </label>
          <button disabled={saving}>{saving ? 'Сохраняем...' : 'Сохранить в MySQL'}</button>
        </form>
      </section>

      <section className="card">
        <div className="section-heading">
          <h2>Сохранённые сообщения</h2>
          <button className="secondary" onClick={loadGreetings}>Обновить</button>
        </div>
        {status && <p className="status">{status}</p>}
        {greetings.length === 0 && !status ? <p className="muted">Пока записей нет.</p> : (
          <ul>
            {greetings.map((greeting) => (
              <li key={greeting.id}>
                <strong>{greeting.name}</strong>
                <span>{greeting.message}</span>
              </li>
            ))}
          </ul>
        )}
      </section>
    </main>
  )
}

createRoot(document.getElementById('root')).render(
  <StrictMode><App /></StrictMode>,
)
