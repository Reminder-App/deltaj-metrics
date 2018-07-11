var fs = require('fs')
var lr = require('readline')
var stringify = require('csv-stringify')

var Class    = 'AddReminderActivity'
var Func     = ''
var FuncType = ''

var Env      = []
var Track    = []

function main () {

  var dirs = fs.readdirSync('./javas')

  // for (dir of dirs) {
    var lineReader = lr.createInterface({
      input: fs.createReadStream('./javas/AddReminderActivity.java')
    })

    lineReader.on('line', (l) => {
      handleLine(l.trim())
    })

    lineReader.on('close', () => {
      // console.log(Env)
      // stringify(Env, function(err, output){
      //   console.log(output)
      // });
      // writeCSV()
    })
  // }

}


function saveOrUpdateEnv (feature) {
  let exists = false

  for (obj of Env) {
    if (obj.feature == feature && obj.jClass == Class) {
      obj.active = true
      exists     = true
    }
  }

  if (!exists) {
    Env.push({
      jClass: Class,
      feature: feature,
      // function: {
      //   type: FuncType,
      //   name: Func
      // },
      counter: 0,
      active: true
    })
  }
}

function handleLine (line) {

  if (line.startsWith('//#if ')) {
    let fs
    let unique = false

    if (line.includes('&&') && line.includes('||')) {
      fs = line.substring(6)
      fs = fs.split(/[^a-zA-Z]/)
             .filter( (v) => v !== '')
             .sort()
             .reduce((init, current) => {
               if (init.length === 0 || init[init.length - 1] !== current) {
                 init.push(current);
               }
               return init;
             }, [])
      Track.push(fs)
    }

    else if (line.includes('&&')) {
      fs = line.substring(6).split(' && ')
      Track.push(fs)
    }

    else if (line.includes('||')) {
      fs = line.substring(6).split(' || ')
      Track.push(fs)
    }

    else {
      fs = line.substring(6)
      unique = true
    }

    if (unique) {
      saveOrUpdateEnv(fs)
    } else {
      for (f of fs) {
        saveOrUpdateEnv(f)
      }
    }

  }

  else if (line.startsWith('//#ifdef ')) {
    const f = line.substring(9)
    Track.push(f)
    saveOrUpdateEnv(f)
  }

  else if (line.startsWith('//#elifdef ')) {
    const f = line.substring(11)
    saveOrUpdateEnv(f)
    setInactive(Track.pop())
    Track.push(f)
  }

  else if (line.startsWith('//#endif')) {
    setInactive(Track.pop())
  }

  else {
    // Count active feature lines
    for (f of Env) {
      if (f.active) {
        f.counter = f.counter + 1
      }
    }
  }

}

function setInactive (feature) {
  if (feature instanceof Array) {
    for (ff of feature) {
      for (f of Env) {
        if (f.feature == ff && f.jClass == Class) {
          f.active = false
        }
      }
    }
  } else {
    for (f of Env) {
      if (f.feature == feature && f.jClass == Class) {
        f.active = false
      }
    }
  }
  console.log(Env)
}


main ()
