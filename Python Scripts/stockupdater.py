#https://pypi.org/project/websocket_client/
import websocket
import mysql.connector
import json

def on_message(ws, message):
  x = json.loads(message)
  price = x['data'][0]['p']
  name = x['data'][0]['s']
  
  query = """UPDATE stockprices
  SET price = %s
  WHERE stockname=%s
  """
  
  t = (price, name)
  mycursor.execute(query, t)
  mydb.commit()
  print("Updated database with price")

def on_error(ws, error):
    print(error)

def on_close(ws):
    print("### closed ###")

def on_open(ws):
    ws.send('{"type":"subscribe","symbol":"AAPL"}')
    ws.send('{"type":"subscribe","symbol":"AMZN"}')
    ws.send('{"type":"subscribe","symbol":"MSFT"}')
    ws.send('{"type":"subscribe","symbol":"TSLA"}')
    #ws.send('{"type":"subscribe","symbol":"BINANCE:BTCUSDT"}')


if __name__ == "__main__":
    #websocket.enableTrace(True)

    mydb = mysql.connector.connect(
      host="localhost",
      user="root",
      password="root",
      database="TrojanTrader"
    )
    mycursor = mydb.cursor(prepared=True)

    ws = websocket.WebSocketApp("wss://ws.finnhub.io?token=cgvj4qpr01qqk0doko70cgvj4qpr01qqk0doko7g",
                              on_message = on_message,
                              on_error = on_error,
                              on_close = on_close)
    ws.on_open = on_open
    ws.run_forever()