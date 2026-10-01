import { StrictMode, useEffect, useState } from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

const API_URL = 'http://localhost:8082/api/greetings'
const warsawTime = new Intl.DateTimeFormat('ru-RU', {
  timeZone: 'Europe/Warsaw',
  dateStyle: 'medium',
  timeStyle: 'medium',
})
const recordedTime = new Intl.DateTimeFormat('ru-RU', {
  timeZone: 'Europe/Warsaw',
  dateStyle: 'short',
  timeStyle: 'medium',
})

function App() {
  const [greetings, setGreetings] = useState([])
  const [status, setStatus] = useState('Загрузка сообщений из SQLite...')
  const [now, setNow] = useState(new Date())

  const loadGreetings = async () => {
    try {
      const response = await fetch(API_URL)
      if (!response.ok) throw new Error(`Сервер ответил с кодом ${response.status}`)
      setGreetings(await response.json())
      setStatus('')
    } catch (error) {
      setStatus(`Ошибка: ${error.message}. Проверьте, запущен ли Spring Boot 3.`)
    }
  }

  useEffect(() => {
    loadGreetings()
    const dataTimer = window.setInterval(loadGreetings, 5000)
    const clockTimer = window.setInterval(() => setNow(new Date()), 1000)
    return () => {
      window.clearInterval(dataTimer)
      window.clearInterval(clockTimer)
    }
  }, [])

  return (
    <main className="page">
      <header className="hero">
        <p className="eyebrow">Микросервис 3 · React + Spring Boot + SQLite</p>
        <h1>Архив сообщений</h1>
        <p>Данные из сервиса 2 сохраняются в SQLite вместе со временем получения.</p>
      </header>

      <section className="dashboard" aria-label="Сообщения и время в Варшаве">
        <figure className="dog">
          <img src="/dog.jpg" alt="Собака" />
          <figcaption>Наш помощник</figcaption>
        </figure>

        <section className="card messages">
          <div className="section-heading">
            <div>
              <p className="eyebrow">Локальная база данных</p>
              <h2>Сохранённые сообщения</h2>
            </div>
            <button className="secondary" onClick={loadGreetings}>Обновить</button>
          </div>
          {status && <p className="status" role="status">{status}</p>}
          {greetings.length === 0 && !status ? <p className="muted">Пока записей нет.</p> : (
            <ul>
              {greetings.map((greeting) => (
                <li key={greeting.sourceId}>
                  <div className="message-heading">
                    <strong>{greeting.name}</strong>
                    <time dateTime={greeting.recordedAt}>
                      Записано: {recordedTime.format(new Date(greeting.recordedAt))}
                    </time>
                  </div>
                  <p>{greeting.message}</p>
                </li>
              ))}
            </ul>
          )}
        </section>

        <aside className="clock" aria-live="off">
          <p className="eyebrow">Часовой пояс Europe/Warsaw</p>
          <h2>В Варшаве сейчас:</h2>
          <time>{warsawTime.format(now)}</time>
        </aside>
      </section>
      <footer>Обновление данных из сервиса 2 каждые 5 секунд</footer>
    </main>
  )
}

createRoot(document.getElementById('root')).render(
  <StrictMode><App /></StrictMode>,
)
