var fs = require('fs')
var lr = require('readline')
var stringify = require('csv-stringify')

var delta = ''

var Env   = []


function main () {

  var dirs = fs.readdirSync('./deltas')
  var counter = 0

  for (dir of dirs) {
    var lineReader = lr.createInterface({
      input: fs.createReadStream('./deltas/'+dir)
    })
  
    lineReader.on('line', (l) => {
      handleLine(l.trim())      
    })

    lineReader.on('close', () => {
      // console.log(Env)
      stringify(Env, function(err, output){
        console.log(output)
      });
      // writeCSV()
    })
  }

}


function saveOrUpdateEnv (feature) {
  let exists = false
  
  for (obj of Env) {
    if (obj.feature == feature && obj.delta == delta) {
      obj.active = true
      exists = true
    }
  }

  if (!exists) {
    Env.push({
      delta: delta,
      feature: feature,
      counter: 0,
      active: true
    })
  }
}

function handleLine (line) {

  if (line.startsWith('delta ')) {
    delta = line.substring(6).split(' {')[0]
  }

  else if (line.startsWith('//#if ')) {
    const fs = line.substring(6).split(' && ')
    for (f of fs) {
      saveOrUpdateEnv(f)  
    }
  }

  else if (line.startsWith('//#ifdef ')) {
    const f = line.substring(9)
    saveOrUpdateEnv(f)    
  }

  else if (line.startsWith('//#endif')) {
    for (f of Env) {
      if (f.active) {
        f.active = false
      }
    }
  }

  // Count active feature lines
  for (f of Env) {
    if (f.active) {
      f.counter = f.counter + 1
    }
  }
}


main ()