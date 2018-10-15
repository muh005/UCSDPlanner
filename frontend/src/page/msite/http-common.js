import axios from 'axios'

export const AXIOS = axios.create({
  baseURL: `http://127.0.0.1:8080`,
  headers: {
    'Access-Control-Allow-Origin': 'http://127.0.0.1:8000'
  }
})