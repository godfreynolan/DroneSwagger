from flask import Flask, jsonify, request

app = Flask(__name__)

@app.route('/drone/<string:id>/<string:actions>', methods=['PUT'])
def send_drone_actions(id, actions):

    print(f"send drone action: {actions}")

    response = {'error': False, 'data': {"id": id, "actions": f"{actions}"}, 'message': 'success'}
    return jsonify(response), 200

@app.route('/drone/<string:id>', methods=['GET'])
def get_drone_by_id(id):

    print(f"get drone by id: {id}")

    response = {'error': False, 'data': {"id": id}, 'message': 'success'}
    return jsonify(response), 200

@app.route('/drone/<string:id>/zoom', methods=['PUT'])
def zoom_in_drone_by_id(id):

    print(f"zoom in drone by id: {id}")

    response = {'error': False, 'data': {"id": id}, 'message': 'success'}
    return jsonify(response), 200

@app.route('/drone/<string:id>/zoom', methods=['DELETE'])
def zoom_out_drone_by_id(id):

    print(f"zoom out drone by id: {id}")

    response = {'error': False, 'data': {"id": id}, 'message': 'success'}
    return jsonify(response), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
