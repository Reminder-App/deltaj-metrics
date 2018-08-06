var fs = require('fs')
var lr = require('readline')
var stringify = require('csv-stringify')

const folder = './deltas-v4/'

// context state
var Env   = []
var Delta = ''

// track nested features
var Track    = []


async function main () {

  var dirs = fs.readdirSync(folder)

  for (dir of dirs) {
    Delta = dir.split('.')[0]
    var lineReader = lr.createInterface({
      input: fs.createReadStream(folder + dir)
    })
  
    await loop (dir)
  }

  stringify(Env, function(err, output){
    console.log(output)
  });

}

function loop (dir) {
  return new Promise( (resolve) => {
    var lineReader = lr.createInterface({
      input: fs.createReadStream(folder + dir)
    })
  
    lineReader.on('line', (l) => {
      handleLine(l.trim())
    })
  
    lineReader.on('close', () => {
      resolve()
    })
  })
}


function saveToEnv (feature) {
  let exists = false
  
  for (obj of Env) {
    if (obj.feature == feature && obj.delta == Delta) {
      exists = true
    }
  }

  if (!exists) {
    Env.push({
      delta: Delta,
      feature: feature,
      counter: 0,
    })
  }
}

function handleLine (line) {

  if (line.startsWith('//#ifdef ')) {
    let fs
    let unique = false
    
    if (line.includes('&&')) {
      fs = line.substring(9).split(' && ') // returns array
      Track.push(fs)
    }
    else if (line.includes('||')) {
      fs = line.substring(9).split(' || ')
      Track.push(fs)
    }
    else {
      fs = line.substring(9)
      Track.push(fs)
      unique = true
    }

    if (unique) {
      saveToEnv(fs)
    }
    else {  
      for (f of fs) {
        saveToEnv(f)  
      }
    }
  }
  else if (line.startsWith('//#endif')) {
    Track.pop()
  }
  else if(line == '') {
    return
  }
  else {
    // count active feature lines
    countLines()
  }
}

function countLines () {
  let i = Track.length - 1
  if (Track[i] instanceof Array) {
    for (ff of Track[i]) {
      for (f of Env) {
        if (f.feature == ff && f.delta == Delta) {
          f.counter = f.counter + 1
        }
      }
    }
  }
  else {
    for (f of Env) {
      if (f.feature == Track[i] && f.delta == Delta) {
        f.counter = f.counter + 1
      }
    }
  }
}


main ()