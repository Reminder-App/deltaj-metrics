var fs = require('fs');
var lr = require('readline');
var stringify = require('csv-stringify');

const folder = "reminder-cc/v4/"

// context state
var Env   = []
var Class = ''

// track nested features
var Track    = []


async function main () {

  var dirs = fs.readdirSync(folder)

  for (dir of dirs) {
    Class = dir.split('.')[0]

    try {
      await loop (dir)
    } catch (e) {
      console.log(e)
    }
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
    if (obj.feature == feature && obj.Class == Class) {
      exists = true
    }
  }

  if (!exists) {
    Env.push({
      Class: Class,
      feature: feature,
      counter: 0,
    })
  }
}

function handleLine (line) {

  if (line.startsWith('//#ifdef ') || line.startsWith('//#if ')) {
    let idelimiter = line.startsWith('//#ifdef ') ? 9 
                   : line.startsWith('//#if ') ? 6 
                   : 6

    let fs
    let unique = false
    
    if (line.includes('&&')) {
      fs = line.substring(idelimiter).split(' && ') // returns array
      Track.push(fs)
    }
    else if (line.includes('||')) {
      fs = line.substring(idelimiter).split(' || ')
      Track.push(fs)
    }
    else {
      fs = line.substring(idelimiter)
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
  var counted = []

  for (ffs of Track) {
    if (ffs instanceof Array) {
      for (ff of ffs) {
        for (f of Env) {
          if (f.feature == ff && f.Class == Class && !counted.includes(ff)) {
            f.counter = f.counter + 1
            counted.push(ff)
          }
        }
      }
    }
    else {
      for (f of Env) {
        if (f.feature == ffs && f.Class == Class && !counted.includes(ffs)) {
          f.counter = f.counter + 1
          counted.push(ffs)
        }
      }
    }

  }
}


main ()